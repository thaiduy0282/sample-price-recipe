package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;

import java.util.Date;
import java.util.List;

public class SimplePricingService {

    public void calculatePriceOneOff(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();

        lineItems.stream()
                .filter(item -> Util.isValidFormula(recipe.getPricingCondition(), item) && Util.isValidFormula(recipe.getAppliedOn(), item))
                .forEach(item -> {
                    // Get the latest discount detail for this LineItem and pricing context
                    DiscountDetails latestDiscount = Util.findLatestDiscountDetail(item.getId(), recipe.getPriceApplicationON(), profilingRequestDTO);
                    // Only apply adjustments if there are existing discounts
                    if (latestDiscount != null) {
                        // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                        double adjustedPrice = Util.calculateAdjustedPrice(
                                latestDiscount.getAfterAdjustment(),
                                recipe.getDealStrategy(),
                                recipe.getApplicationType(),
                                String.valueOf(recipe.getApplicationValue())
                        );

                        // Determine the next sequence number for the discount
                        int nextSequence = latestDiscount.getSequence() + 1;

                        // Create and add a new DiscountDetails entry to the profiling request
                        Util.createAndAddDiscountDetails(item, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, recipe, profilingRequestDTO);
                    }
                });
    }
}
