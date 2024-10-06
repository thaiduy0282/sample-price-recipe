package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FormulaEvaluatorTests {

    @Test
        void testFormulaEvaluator() {
            String formula1 = "(product.type == \"standalone\" && category.name == \"Hardware Product\")"
                    + " || product.tag.bg == \"CNS\""
                    + " || lineItem.quantity > 10"
                    + " || lineItem.locationName == \"HYDERABAD\"";

            String formula2 = "(product.type == \"standalone\" && category.name == \"Hardware Product\")"
                    + " && product.tag.bg == \"CNS\""
                    + " && lineItem.quantity > 10"
                    + " && lineItem.locationName == \"HYDERABAD\"";

            assertTrue(com.example.demo.utils.FormulaEvaluator.evaluateFormula(formula1, "lineItemId"));
            assertFalse(com.example.demo.utils.FormulaEvaluator.evaluateFormula(formula2, "lineItemId"));
        }

    @Test
        void testConvertPropertyToGetter(){
            String formula1 = "(product.type == \"standalone\" && category.name == \"Hardware Product\")"
                + " || product.tag.bg == \"CNS\""
                + " || lineItem.quantity > 10"
                + " || lineItem.locationName == \"HYDERABAD\"";

            String expression1 = "(product.getType() == \"standalone\" && category.getName() == \"Hardware Product\")"
                    + " || product.getTag().getBg() == \"CNS\""
                    + " || lineItem.getQuantity() > 10"
                    + " || lineItem.getLocationName() == \"HYDERABAD\"";

            assertEquals(com.example.demo.utils.FormulaEvaluator.convertPropertyToGetter(formula1), expression1);

            String formula2 = "(product.type == \"standalone\" && category.name == \"Hardware Product\")"
                + " && product.tag.bg == \"CNS\""
                + " && lineItem.quantity > 10"
                + " && lineItem.locationName == \"12 Parker St. Ballarat\"";

            String expression2 = "(product.getType() == \"standalone\" && category.getName() == \"Hardware Product\")"
                + " && product.getTag().getBg() == \"CNS\""
                + " && lineItem.getQuantity() > 10"
                + " && lineItem.getLocationName() == \"12 Parker St. Ballarat\"";

            assertEquals(com.example.demo.utils.FormulaEvaluator.convertPropertyToGetter(formula2), expression2);

        }

    @Test
        void testAddPropertyToContext(){
            Map<String, Object> context = new HashMap<>();
            com.example.demo.utils.FormulaEvaluator.addPropertyToContext("product", context, "lineItemId");
            com.example.demo.utils.FormulaEvaluator.addPropertyToContext("category", context, "lineItemId");
            com.example.demo.utils.FormulaEvaluator.addPropertyToContext("lineItem", context, "lineItemId");

            Map<String,Object> trueContext = new HashMap<>();
            trueContext.put("product", com.example.demo.services.LineItemService.prodTrue);
            trueContext.put("category", com.example.demo.services.LineItemService.categoryTrue);
            trueContext.put("lineItem", com.example.demo.services.LineItemService.lineItemTrue);

            assertEquals(context, trueContext);

            // context1 should throw exception with message "NoSuchMethodException on reflection call getNonExistPropertyById method"
            RuntimeException handledException = assertThrows(
                    RuntimeException.class,
                    () -> com.example.demo.utils.FormulaEvaluator.addPropertyToContext("nonExistProperty", context, "lineItemId")
            );

            assertEquals(handledException.getMessage(), "NoSuchMethodException on reflection call getNonExistPropertyById method");
        }


}
