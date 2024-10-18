package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.models.buyXGetY.Adjustment;
import com.example.demo.models.buyXGetY.BuyConditionGroup;
import com.example.demo.models.buyXGetY.Condition;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuyXGetYService {

    public void calculatePriceOneOff(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();

        // Collect all possible adjustments from the recipe
        Map<List<Adjustment>, Double> possibleAdjustments = calculateAllPossibleAdjustments(priceRecipe, profilingRequestDTO);

        // Sort adjustments by their total value to maximize discounts or minimize markups
        List<Map.Entry<List<Adjustment>, Double>> possibleAdjustmentsSorted = possibleAdjustments.entrySet().stream()
            .sorted(Map.Entry.comparingByValue()) // Ascending order: Smallest adjustment first
            .toList(); // Collect to list

        // Apply the best adjustment to the cart
        if (!possibleAdjustments.isEmpty()) {
            val bestListAdjustment = possibleAdjustmentsSorted.getFirst().getKey();
            System.out.println("Applying best adjustment: " + bestListAdjustment);
            applyAdjustment(lineItems, bestListAdjustment, priceRecipe, profilingRequestDTO);
        }
    }

    /**
     * Find all possible adjustments from the recipe.
     */
    private Map<List<Adjustment>, Double> calculateAllPossibleAdjustments(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        Map<List<Adjustment>, Double> adjustments = new HashMap<>();
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();

        for (BuyConditionGroup group : recipe.getConditionGroups()) {
            val conditions = group.getBuySection().getConditions();
            if (isSatisfied(lineItems, conditions)) {
                int applicableTimes = maxApplicableTimes(lineItems, conditions);
                adjustments.putAll(Util.createAdjustment(recipe, profilingRequestDTO, group, applicableTimes));
            }
        }
        return adjustments;
    }

    /**
     * Apply the given adjustment to the lineItems.
     */
    private void applyAdjustment(List<LineItem> lineItems, List<Adjustment> adjustments, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        // Create a map of productId -> Adjustment to quickly find the relevant adjustment for each product
        Map<String, Adjustment> adjustmentMap = adjustments.stream()
                .collect(Collectors.toMap(Adjustment::getProductId, adjustment -> adjustment));

        for (LineItem item : lineItems) {
            Adjustment adjustment = adjustmentMap.get(item.getProductId());

            if (adjustment != null) {
                applyBestAdjustment(item, adjustment, priceRecipe, profilingRequestDTO);
            }
        }
    }

    public boolean isSatisfied(List<LineItem> cart, List<Condition> buyConditions) {
        return buyConditions.stream().allMatch(condition -> Util.matchesCondition(condition, cart));
    }

    public int maxApplicableTimes(List<LineItem> cart, List<Condition> buyConditions) {
        return buyConditions.stream()
            .mapToInt(condition -> Util.maxApplicableTimes(cart, condition))
            .min()
            .orElse(0);
    }

    private void applyBestAdjustment(LineItem item, Adjustment adjustment, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        // Get the latest discount detail for this LineItem and pricing context
        DiscountDetails latestDiscount = Util.findLatestDiscountDetail(item.getId(), priceRecipe.getPriceApplicationON(), profilingRequestDTO);

        // Only apply adjustments if there are existing discounts
        if (latestDiscount != null) {

            // Calculate the adjusted price using the deal strategy and application details
            double adjustedPrice = adjustment.getApplicationValue();

            // Determine the next sequence number for the discount
            int nextSequence = latestDiscount.getSequence() + 1;

            // Create and add a new DiscountDetails entry to the profiling request
            Util.createAndAddDiscountDetails(item, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequestDTO);
        }
    }

}
