package com.example.demo.service;

import com.example.demo.models.*;
import com.example.demo.utils.FormulaEvaluator;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SimplePricingOneOffService {

    public void calculatePrice(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        var applicationType = recipe.getApplicationType();
        var applicationValue = recipe.getApplicationValue();

        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        List<DiscountDetails> discountDetailsList = profilingRequestDTO.getDiscountDetails();

        lineItems.stream()
                .filter(item -> isValidFormula(recipe.getPricingCondition(), item.getId()) && isValidFormula(recipe.getAppliedOn(), item.getId()))
                .forEach(item -> {
                    Optional<DiscountDetails> lastDiscountDetailsOfLineItem = getLastDiscountDetailsOfLineItem(
                            recipe, item, discountDetailsList);
                    if (lastDiscountDetailsOfLineItem.isEmpty()) {
                        return;
                    }
                    double originalPrice = lastDiscountDetailsOfLineItem.get().getAfterAdjustment();
                    int nextSequence = lastDiscountDetailsOfLineItem.get().getSequence() + 1;
                    double adjustedPrice = calculateAdjustedPrice(originalPrice, applicationType, applicationValue);
                    insertDiscountDetail(discountDetailsList, item, recipe, originalPrice, adjustedPrice, nextSequence);
                });
    }

    private boolean isValidFormula(String formula, String lineItemId) {
        return !StringUtils.hasLength(formula) ||
                FormulaEvaluator.evaluateFormula(formula, lineItemId);
    }

    private Optional<DiscountDetails> getLastDiscountDetailsOfLineItem(PriceRecipe recipe, LineItem lineItem, List<DiscountDetails> discountDetails) {
        return discountDetails.stream()
                .filter(discount -> discount.getLineItemId().equals(lineItem.getId())
                        && discount.getName().equals(recipe.getPriceAppliedTo()))
                .max(Comparator.comparingInt(DiscountDetails::getSequence));
    }

    private void insertDiscountDetail(List<DiscountDetails> discountDetailsList,
                                      LineItem item, PriceRecipe recipe,
                                      double originalPrice, double adjustedPrice, int nextSequence) {
        DiscountDetails discountDetails = DiscountDetails.builder()
                .adjustmentType(recipe.getApplicationType())
                .adjustmentValue(recipe.getApplicationValue())
                .appliedOnAmount(originalPrice)
                .afterAdjustment(adjustedPrice)
                .netPrice(item.getNetPrice())
                .discountDate(new Date().getTime())
                .discountSource("Recipe")
                .productConfigurationId(item.getProductId())
                .lineItemId(item.getId())
                .sequence(nextSequence)
                .recipeId(recipe.getId())
                .appliedTo(recipe.getPriceAppliedTo())
                .name(recipe.getPriceAppliedTo())
                .build();

        discountDetailsList.add(discountDetails);
    }

    private static double calculateAdjustedPrice(double originalPrice, String applicationType, double applicationValue) {
        double adjustedPrice = originalPrice;

        if ("%".equals(applicationType)) {
            adjustedPrice = originalPrice - (originalPrice * (applicationValue / 100));
        } else if ("amount".equals(applicationType)) {
            adjustedPrice = originalPrice - applicationValue;
        }

        return adjustedPrice;
    }
}
