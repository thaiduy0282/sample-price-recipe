package com.example.demo.utils;

import com.example.demo.common.Constants;
import com.example.demo.models.LineItem;
import com.example.demo.services.CosmosDbService;
import com.example.demo.services.LineItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaEvaluator {
    private static final Logger log = LogManager.getLogger(FormulaEvaluator.class);

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

    public static Map<String, Object> constructMvelContext1(Set<String> targetObjects, String lineItemId) {
        LineItem lineItem = LineItemService.getLineItemById(lineItemId);

        Map<String, Object> context = new HashMap<>();

        // Add line item object to the context map
        for (Field field : LineItem.class.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();

            try {
                Object fieldValue = field.get(lineItem);
                context.put(fieldName, fieldValue);
            } catch (IllegalAccessException e) {
                log.error("Skip field: {}", fieldName);
                log.error(e.toString());
            }
        }

        // Check if the target object is a member object of the line item
        for (String targetObject : targetObjects) {
            if (!context.containsKey(targetObject)) {
                throw new IllegalArgumentException(String.format("Target object '%s' not found in LineItem", targetObject));
            }
        }

        return context;
    }

    private static String upperFirstLetter(String str){
        if (str == null || str.isEmpty()) {
            return str;
        }

        char[] charArray = str.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        return new String(charArray);
    }

    /**
     * Adds a property object to the context map.
     * For example, if the target object is "product", the context map will contain { "product", LineItemService.getProductById(lineItemId) }.
     *
     * @param targetObject the target object to add to the context map
     * @param context the context map to add the target object to
     * @param lineItemId the line item ID to fetch the target object
     */
    public static void addPropertyToContext(String targetObject, Map<String, Object> context, String lineItemId) {
        String targetMethodName = "get" + upperFirstLetter(targetObject) + "ById";

        try {
            Class<?> lineItemServiceClass = Class.forName(Constants.LINE_ITEM_SERVICE_PATH);
            Method targetMethod = lineItemServiceClass.getDeclaredMethod(targetMethodName, String.class);

            Object lineItemServiceInstance = lineItemServiceClass.getDeclaredConstructor().newInstance();
            Object result = targetMethod.invoke(lineItemServiceInstance, lineItemId);

            context.put(targetObject, result);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException on reflection call LineItemService class");
        }catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("NoSuchMethodException on reflection call %s method", targetMethodName));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.toString());
            throw new RuntimeException("Exception on reflection call LineItemService instance");
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
//        String expression = convertPropertyToGetter(formula);
        Set<String> targetObjects = extractProperties(formula);

        Map<String, Object> vars = constructMvelContext(targetObjects, lineItemId);

        if(vars.isEmpty()){
            throw new IllegalArgumentException("No target objects found in the formula");
        }

        Serializable compiled = MVEL.compileExpression(formula);
        return (boolean) MVEL.executeExpression(compiled, vars);
    }

    public static Map<String, Object> constructMvelContext2(Set<String> targetObjects, LineItem lineItem) {
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

    public static boolean evaluateFormula2(String formula, LineItem lineItem){
        Set<String> targetObjects = extractProperties(formula);

        Map<String, Object> vars = constructMvelContext2(targetObjects, lineItem);

        if(targetObjects.contains(Constants.LINE_ITEM_PROPERTY)){
            vars.put(Constants.LINE_ITEM_PROPERTY, lineItem);
        }

        Serializable compiled = MVEL.compileExpression(formula);
        return (boolean) MVEL.executeExpression(compiled, vars);
    }
}
