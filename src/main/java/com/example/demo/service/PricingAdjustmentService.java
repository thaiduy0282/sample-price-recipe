package com.example.demo.service;

import com.example.demo.models.*;

import java.util.*;
/**
 * This class is responsible for processing pricing adjustments for LineItems based on specific rules defined in PriceRecipe.
 */
public class PricingAdjustmentService {
    /**
     * Calculates cumulative price adjustments for each group of LineItems based on their timeDimensionName.
     * If the timeDimensionName matches the sourceDimensionName in the PriceRecipeRange and the total quantity
     * falls within the startTier and endTier, then adjustments (discount/markup) are applied to the netPrice.
     *
     * @param priceRecipe The PriceRecipe containing multiple PriceRecipeRanges with pricing strategies.
     * @param profilingRequest The request object containing a list of LineItems to be processed.
     */
    public void calculateCumulativeRange(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        // Group LineItems by their timeDimensionName
        Map<String, List<LineItem>> groupedLineItems = groupLineItemsByTimeDimension(profilingRequest.getLineItems(), priceRecipe.getRanges());

        // Iterate over each group of LineItems
        for (Map.Entry<String, List<LineItem>> entry : groupedLineItems.entrySet()) {
            String timeDimensionName = entry.getKey();
            List<LineItem> items = entry.getValue();
            // Apply price adjustments for groups that satisfy the conditions
            applyPriceAdjustments(priceRecipe, timeDimensionName, items, profilingRequest);
        }
    }

    /**
     * Groups the provided list of LineItems by their timeDimensionName based on the associated PriceRecipeRanges.
     *
     * @param lineItems List of LineItems to be grouped.
     * @param priceRecipeRanges List of PriceRecipeRange objects defining the grouping criteria.
     * @return A map where the key is the timeDimensionName, and the value is the list of LineItems that belong to that group.
     */
    private Map<String, List<LineItem>> groupLineItemsByTimeDimension(List<LineItem> lineItems, List<PriceRecipeRange> priceRecipeRanges) {
        Map<String, List<LineItem>> groupedLineItems = new HashMap<>();

        // Iterate over each PriceRecipeRange to determine grouping criteria
        for (PriceRecipeRange range : priceRecipeRanges) {
            List<String> sourceDimensionNames = range.getSourceDimensionName();

            // Check each LineItem to see if it matches the grouping criteria
            for (LineItem lineItem : lineItems) {
                String timeDimensionName = lineItem.getTimeDimensionName();
                if (sourceDimensionNames.contains(timeDimensionName)) {
                    // Group LineItems by timeDimensionName
                    groupedLineItems.computeIfAbsent(timeDimensionName, k -> new ArrayList<>()).add(lineItem);
                }
            }
        }
        return groupedLineItems;
    }

    /**
     * Applies price adjustments (discount/markup) to the grouped LineItems if the group meets the specified conditions.
     *
     * @param priceRecipe The PriceRecipe containing price adjustment rules.
     * @param items The list of LineItems that belong to the current group.
     * @param profilingRequest The request object containing discount details.
     */
    private void applyPriceAdjustments(PriceRecipe priceRecipe, String timeDimensionName, List<LineItem> items, ProfilingRequestDTO profilingRequest) {
        List<PriceRecipeRange> priceRecipeRanges = priceRecipe.getRanges();

        // Calculate the total quantity for the current group
        double totalQuantity = calculateTotalQuantity(items);

        // Check each PriceRecipeRange to see if it matches the quantity criteria
        for (PriceRecipeRange range : priceRecipeRanges) {
            if (range.getSourceDimensionName().contains(timeDimensionName) && isQuantityInRange(totalQuantity, range)) {
                // Apply adjustments for each LineItem in the group
                for (LineItem item : items) {
                    double adjustedPrice = calculateAdjustedPrice(item.getNetPrice(), range.getDealStrategy(), range.getApplicationType(), range.getApplicationValue());
                    int nextSequence = (profilingRequest.getDiscountDetails() != null && !profilingRequest.getDiscountDetails().isEmpty())
                            ? profilingRequest.getDiscountDetails().getLast().getSequence() + 1
                            : 0;
                    DiscountDetails discountDetails = new DiscountDetails(
                            range.getApplicationType(),
                            Double.parseDouble(range.getApplicationValue()),
                            item.getNetPrice(),
                            adjustedPrice,
                            0d,
                            new Date().getTime(),
                            null,
                            null,
                            item.getProductId(),
                            item.getId(),
                            nextSequence,
                            null,
                            priceRecipe.getId(),
                            priceRecipe.getPriceAppliedTo()
                    );

                    // Add discount details to the profiling request
                    profilingRequest.getDiscountDetails().add(discountDetails);
                }
            }
        }
    }

    /**
     * Calculates the total quantity for a list of LineItems.
     *
     * @param items List of LineItems for which the total quantity is calculated.
     * @return The total quantity of the LineItems.
     */
    private double calculateTotalQuantity(List<LineItem> items) {
        // Sum up the quantity for each LineItem
        return items.stream().mapToDouble(LineItem::getQuantity).sum();
    }

    /**
     * Checks whether the total quantity falls within the specified startTier and endTier range.
     *
     * @param totalQuantity The total quantity to be checked.
     * @param range The PriceRecipeRange that defines the startTier and endTier.
     * @return True if the total quantity is within the range, false otherwise.
     */
    private boolean isQuantityInRange(double totalQuantity, PriceRecipeRange range) {
        return totalQuantity >= range.getStartTier() && totalQuantity <= range.getEndTier();
    }

    /**
     * Calculates the adjusted price based on the deal strategy and application type.
     *
     * @param netPrice The original price of the item.
     * @param dealStrategy The strategy for price adjustment (discount or markup).
     * @param applicationType The type of adjustment (% or Amount).
     * @param applicationValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the discount or markup.
     */
    private double calculateAdjustedPrice(double netPrice, String dealStrategy, String applicationType, String applicationValue) {
        double adjustmentValue = Double.parseDouble(applicationValue);

        // Apply discount or markup based on the dealStrategy
        switch (dealStrategy.toLowerCase()) {
            case "discount":
                return applyDiscount(netPrice, applicationType, adjustmentValue);
            case "markup":
                return applyMarkup(netPrice, applicationType, adjustmentValue);
            default:
                // If no valid dealStrategy is provided, return the original netPrice
                return netPrice;
        }
    }

    /**
     * Applies a discount to the net price based on the application type.
     *
     * @param netPrice The original price of the item.
     * @param applicationType The type of adjustment (% or Amount).
     * @param adjustmentValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the discount.
     */
    private double applyDiscount(double netPrice, String applicationType, double adjustmentValue) {
        if ("%".equalsIgnoreCase(applicationType)) {
            // Apply percentage-based discount
            return netPrice - (netPrice * (adjustmentValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            // Apply fixed amount discount
            return netPrice - adjustmentValue;
        }
        // Return the original netPrice if no valid applicationType is provided
        return netPrice;
    }

    /**
     * Applies a markup to the net price based on the application type.
     *
     * @param netPrice The original price of the item.
     * @param applicationType The type of adjustment (% or Amount).
     * @param adjustmentValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the markup.
     */
    private double applyMarkup(double netPrice, String applicationType, double adjustmentValue) {
        if ("%".equalsIgnoreCase(applicationType)) {
            // Apply percentage-based markup
            return netPrice + (netPrice * (adjustmentValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            // Apply fixed amount markup
            return netPrice + adjustmentValue;
        }
        // Return the original netPrice if no valid applicationType is provided
        return netPrice;
    }
}
