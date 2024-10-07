package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.utils.FormulaEvaluator;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

public class SimplePricingOneOffService {

    public void calculatePrice(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        var applicationType = recipe.getApplicationType();
        var applicationValue = recipe.getApplicationValue();

        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        List<DiscountDetails> discountDetailsList = profilingRequestDTO.getDiscountDetails();

        lineItems.stream()
                .filter(item -> isValidFormula(recipe.getPricingCondition(), item.getId()) && isValidFormula(recipe.getAppliedOn(), item.getId()))
                .forEach(item -> {
                    DiscountDetails lastDiscountDetailsOfLineItem = Util.findLatestDiscountDetail(
                            item.getId(),
                            recipe.getPriceApplicationON(),
                            profilingRequestDTO
                    );
                    if (null == lastDiscountDetailsOfLineItem) {
                        return;
                    }
                    double originalPrice = lastDiscountDetailsOfLineItem.getAfterAdjustment();
                    int nextSequence = lastDiscountDetailsOfLineItem.getSequence() + 1;
                    double adjustedPrice = calculateAdjustedPrice(recipe.getDealStrategy(), originalPrice,
                            applicationType, applicationValue);
                    insertDiscountDetail(discountDetailsList, item, recipe, originalPrice, adjustedPrice, nextSequence);
                });
    }

    private boolean isValidFormula(String formula, String lineItemId) {
        return !StringUtils.hasLength(formula) ||
                FormulaEvaluator.evaluateFormula(formula, lineItemId);
    }

    private void insertDiscountDetail(List<DiscountDetails> discountDetailsList,
                                      LineItem item, PriceRecipe recipe,
                                      double originalPrice, double adjustedPrice, int nextSequence) {
        DiscountDetails discountDetails = new DiscountDetails(
                recipe.getApplicationType(), // Type of application (Discount/Markup)
                recipe.getApplicationValue(), // Value applied (e.g., percentage or amount)
                originalPrice, // Original price before adjustment
                adjustedPrice, // The calculated adjusted price after applying discount or markup
                0d, // Placeholder for an unspecified value; to be corrected for the real case
                new Date().getTime(), // Timestamp of the discount application; to be corrected for the real case
                "Recipe", // Placeholder for a more descriptive name; to be corrected for the real case
                null, // Placeholder for a reference; to be corrected for the real case
                item.getProductId(), // The product ID from the line item
                item.getId(), // The line item ID
                nextSequence, // Sequence number to determine the order of discount details
                null, // Placeholder for additional information; to be corrected for the real case
                recipe.getId(), // ID of the price recipe
                recipe.getPriceAppliedTo() // The target of the price application (e.g., product or service)
        );
        // Set the name of the discount details based on the price application's original price
        discountDetails.setName(recipe.getPriceAppliedTo());

        discountDetailsList.add(discountDetails);
    }

    private double calculateAdjustedPrice(String dealStrategy, double originalPrice, String applicationType, double applicationValue) {
        return switch (dealStrategy.toLowerCase()) {
            case "discount" -> applyDiscount(originalPrice, applicationType, applicationValue);
            case "markup" -> applyMarkup(originalPrice, applicationType, applicationValue);
            default -> originalPrice;
        };
    }

    private double applyDiscount(double originalPrice, String applicationType, double applicationValue) {
        if ("Percentage".equalsIgnoreCase(applicationType)) {
            return originalPrice - (originalPrice * (applicationValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            return originalPrice - applicationValue;
        }
        return originalPrice;
    }

    private double applyMarkup(double originalPrice, String applicationType, double applicationValue) {
        if ("Percentage".equalsIgnoreCase(applicationType)) {
            return originalPrice + (originalPrice * (applicationValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            return originalPrice + applicationValue;
        }
        return originalPrice;
    }

}
