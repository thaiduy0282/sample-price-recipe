package com.example.demo.utils;

import ai.qworks.dao.nontransaction.ProductCategoryAssc;
import com.example.demo.models.LineItem;
import com.example.demo.common.Constants;
import com.example.demo.services.CosmosDbService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaEvaluator {
    private static final Logger log = LogManager.getLogger(FormulaEvaluator.class);

    /**
     * Extracts the unique member object references from a formula.
     * For example, "product.productFamily && category.kind.name" will return a set containing "product, category".
     * Note: This method may fail with unexpected '.' character in the formula.
     *
     * @param formula the formula containing member object references
     * @return a set of unique member object references
     */
    public static Set<String> extractProperties(String formula) {
        // Regular expression to find member object references with dots
        Pattern pattern = Pattern.compile("\\w+\\.\\w+");

        // Use a set to store unique member object references
        Set<String> memberObjects = new HashSet<>();

        Matcher matcher = pattern.matcher(formula);
        while (matcher.find()) {
            memberObjects.add(matcher.group());
        }

        Set<String> targetObjects = new HashSet<>();
        for (String memberObject : memberObjects) {
            String[] parts = memberObject.split("\\.");

            if(StringUtils.isNotBlank(parts[0])){
                targetObjects.add(parts[0]);
            }
        }

        return targetObjects;
    }
    
    private static void getContextForCategory(LineItem lineItem, Map<String, Object> context) {
        Map<String, Object> filterProductCategoryAssc = new HashMap<>();
        filterProductCategoryAssc.put("productId", lineItem.getProductId());
        Map<String, Object> productCategoryAsscContext = new HashMap<>();
        CosmosDbService.addEntityToContext(Constants.PRODUCT_CATEGORY_ASSC_PROPERTY, filterProductCategoryAssc, productCategoryAsscContext);

        ProductCategoryAssc productCategoryAssc = (ProductCategoryAssc) productCategoryAsscContext.get(Constants.PRODUCT_CATEGORY_ASSC_PROPERTY);
        Map<String, Object> categoryFilter = new HashMap<>();
        categoryFilter.put("id", productCategoryAssc.getCategoryId());
        CosmosDbService.addEntityToContext(Constants.CATEGORY_PROPERTY, categoryFilter, context);
    }

    private static Map<String, Object> getContextFromLineItem(LineItem lineItem, String targetObject) throws NoSuchFieldException, IllegalAccessException {
        Field field = LineItem.class.getDeclaredField(targetObject + "Id");
        field.setAccessible(true);
        Object fieldValue = field.get(lineItem);

        if (fieldValue == null) {
            throw new IllegalArgumentException(String.format("No value in LineItem's field: '%s'", targetObject));
        }

        Map<String, Object> filter = new HashMap<>();
        filter.put("id", fieldValue);
        return filter;
    }

    /**
     * Constructs a MVEL context map for evaluating a formula.
     * The context map contains the member object references and their corresponding entity objects.
     *
     * @param targetObjects the set of unique member object references
     * @param lineItem the line item object
     * @return a MVEL context map
     */
    public static Map<String, Object> constructMvelContext(Set<String> targetObjects, LineItem lineItem) {
        Map<String, Object> context = new HashMap<>();

        for (String targetObject : targetObjects) {
            switch (targetObject){
                case Constants.LINE_ITEM_PROPERTY -> context.put(Constants.LINE_ITEM_PROPERTY, lineItem); // init data for line item
                case Constants.CATEGORY_PROPERTY  -> getContextForCategory(lineItem, context); // specific for category property only
                default -> {
                    try {
                        // dynamic fetching object base on propertyId in the line item
                        Map<String, Object> filter = getContextFromLineItem(lineItem, targetObject);
                        CosmosDbService.addEntityToContext(targetObject, filter, context);
                    } catch (NoSuchFieldException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(String.format("Field '%sId' not found in LineItem", targetObject));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }

        return context;
    }

    /**
     * Evaluates a formula with the given line item object.
     *
     * @param formula the formula to evaluate
     * @param lineItem the line item object
     * @return the result of the formula evaluation
     */
    public static boolean evaluateFormula(String formula, LineItem lineItem){
        Set<String> targetObjects = extractProperties(formula);

        Map<String, Object> vars = constructMvelContext(targetObjects, lineItem);

        Serializable compiled = MVEL.compileExpression(formula);

        return (boolean) MVEL.executeExpression(compiled, vars);
    }
}
