package com.example.demo.utils;

import com.example.demo.common.Constants;
import com.example.demo.models.LineItem;
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


    public static Map<String, Object> constructMvelContext(Set<String> targetObjects, LineItem lineItem) {
        Map<String, Object> context = new HashMap<>();

        for (String targetObject : targetObjects) {
            if (Constants.LINE_ITEM_PROPERTY.equalsIgnoreCase(targetObject)) {
                continue;
            }

            try {
                Field field = LineItem.class.getDeclaredField(targetObject + "Id");
                field.setAccessible(true);
                Object fieldValue = field.get(lineItem);

                if (fieldValue == null) {
                    throw new IllegalArgumentException(String.format("Field '%s' not found in LineItem", targetObject));
                }

                Map<String, Object> filter = new HashMap<>();
                filter.put("id", fieldValue);

                CosmosDbService.addEntityToContext(targetObject, filter, context);
            } catch (NoSuchFieldException e) {
                log.error(e.getMessage());
                throw new RuntimeException(String.format("NoSuchFieldException: '%sId' not found in LineItem", targetObject));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new RuntimeException(String.format("IllegalAccessException: can not get value of field '%sId'", targetObject));
            }
        }

        return context;
    }

    public static boolean evaluateFormula(String formula, LineItem lineItem){
        Set<String> targetObjects = extractProperties(formula);

        Map<String, Object> vars = constructMvelContext(targetObjects, lineItem);

        if(targetObjects.contains(Constants.LINE_ITEM_PROPERTY)){
            vars.put(Constants.LINE_ITEM_PROPERTY, lineItem);
        }

        Serializable compiled = MVEL.compileExpression(formula);

        return (boolean) MVEL.executeExpression(compiled, vars);
    }
}
