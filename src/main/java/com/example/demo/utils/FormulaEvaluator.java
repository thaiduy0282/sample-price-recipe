package com.example.demo.utils;

import com.example.demo.models.Category;
import com.example.demo.models.LineItem;
import com.example.demo.models.Product;
import com.example.demo.services.LineItemService;
import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaEvaluator {
    public static String capitalizeAfterDot(String input) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false; // Flag to indicate next character should be uppercase

        for (char c : input.toCharArray()) {
            if (c == '.') {
                capitalizeNext = true;
                sb.append(c);
            } else {
                sb.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false; // Reset flag after capitalizing
            }
        }
        return sb.toString();
    }

    /**
     * Converts a property reference to a getter method call.
     * For example, "product.productFamily" will be converted to "product.getProductFamily()".
     * Note: This method may fail with unexpected '.' character in the formula.
     *
     * @param formula the formula containing property references
     * @return the formula with property references converted to getter method calls
     */
    public static String convertPropertyToGetter(String formula) {
        String capitalized = capitalizeAfterDot(formula);
        return capitalized.replaceAll("\\.(\\w+)", ".get$1()");
    }

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

    /**
     * Constructs a MVEL context map with the target objects.
     * For example, if the target objects are "product, category", the context map will contain "product" and "category" objects with data.
     *
     * @param targetObjects the target objects to include in the context map
     * @return a MVEL context map with the target objects
     */
    public static Map<String, Object> constructMvelContext(Set<String> targetObjects, String lineItemId) {
        Map<String, Object> context = new HashMap<>();
        for (String targetObject : targetObjects) {
            addPropertyToContext(targetObject, context, lineItemId);
        }
        return context;
    }

    // MODIFY this function in real use case
    public static void addPropertyToContext(String targetObject, Map<String, Object> context, String lineItemId) {
        switch (targetObject) {
            case "product":
                Product product = LineItemService.getProductById(lineItemId);
                context.put("product", product);
                break;
            case "lineItem":
                LineItem lineItem = LineItemService.getLineItemById(lineItemId);
                context.put("lineItem", lineItem);
                break;
            case "category":
                Category category = LineItemService.getCategoryById(lineItemId);
                context.put("category", category);
                break;
            default:
                throw new IllegalArgumentException("Unknown target object: " + targetObject);
        }
    }

    /**
     * Evaluate input formula.
     * For example, "product.type == \"standalone\""
     *
     * @param formula the formula to parse
     * @return true if the formula is valid, false otherwise
     */
    public static boolean evaluateFormula(String formula, String lineItemId) {
        String expression = convertPropertyToGetter(formula);

        Set<String> targetObjects = extractProperties(formula);

        Map<String, Object> vars = constructMvelContext(targetObjects, lineItemId);

        if(vars.isEmpty()){
            throw new IllegalArgumentException("No target objects found in the formula");
        }

        Serializable compiled = MVEL.compileExpression(expression);
        return (boolean) MVEL.executeExpression(compiled, vars);
    }
}
