package com.example.demo.services.pricingstrategy.impl;

import com.example.demo.enums.LineItemPricesSetter;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.services.pricingstrategy.PriceSettingStrategy;
import com.example.demo.utils.FormulaEvaluator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;

public class SimplePricingOneOffStrategy implements PriceSettingStrategy {

    @Override
    public void calculatePrice(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        var applicationOn = recipe.getPriceApplicationON();
        var appliedTo = recipe.getPriceAppliedTo();
        var applicationType = recipe.getApplicationType();
        var applicationValue = recipe.getApplicationValue();

        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        List<DiscountDetails> discountDetailsList = profilingRequestDTO.getDiscountDetails();

        for (LineItem item : lineItems) {

            if (!FormulaEvaluator.evaluateFormula(recipe.getPricingCondition(), String.valueOf(item.getId()))) {
                continue;
            }

            double originalPrice = getFieldValueByReflection(item, applicationOn);

            double adjustedPrice = calculateAdjustedPrice(originalPrice, applicationType, applicationValue);

            setFieldValueByReflection(item, appliedTo, adjustedPrice);

            insertDiscountDetail(discountDetailsList, item, recipe, originalPrice, adjustedPrice);
        }
    }

    private void insertDiscountDetail(List<DiscountDetails> discountDetailsList,
                                      LineItem item, PriceRecipe recipe,
                                      double originalPrice, double adjustedPrice) {
        DiscountDetails discountDetails = new DiscountDetails();
        discountDetails.setAdjustmentType(recipe.getApplicationType());
        discountDetails.setAdjustmentValue(recipe.getApplicationValue());
        discountDetails.setAppliedOnAmount(originalPrice);
        discountDetails.setAfterAdjustment(adjustedPrice);
//            discountDetails.setDiscountSource();
        discountDetails.setProductConfigurationId(item.getModel());
        discountDetails.setLineItemId(String.valueOf(item.getId()));
        discountDetails.setSequence(discountDetailsList.size() + 1);
        discountDetails.setRecipeId(String.valueOf(recipe.getId()));
        discountDetails.setAppliedTo(recipe.getPriceAppliedTo());


        discountDetailsList.add(discountDetails);
    }

    private double getFieldValueByReflection(LineItem item, String fieldName) {
        try {
            String getterName = "get" +
                    fieldName.substring(0, 1).toUpperCase() +
                    fieldName.substring(1);
            Method getter = LineItem.class.getMethod(getterName);
            return (double) getter.invoke(item);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error accessing getter for field: " + fieldName, e);
        }
    }

    private void setFieldValueByReflection(LineItem item, String fieldName, double value) {
        BiConsumer<LineItem, Double> setter = LineItemPricesSetter.getSetterByFieldName(fieldName);
        setter.accept(item, value);
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
