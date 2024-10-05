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
        Map<String, List<LineItem>> groupedLineItems = groupLineItemsByDimension(profilingRequest.getLineItems(), priceRecipe.getRanges());

        // Iterate over each group of LineItems
        for (Map.Entry<String, List<LineItem>> entry : groupedLineItems.entrySet()) {
            String timeDimensionName = entry.getKey();
            List<LineItem> items = entry.getValue();
            List<PriceRecipeRange> priceRecipeRanges = priceRecipe.getRanges().stream()
                    .filter(r -> r.getTargetDimensionName().contains(timeDimensionName)).toList();
            // Apply price adjustments for groups
            applyPriceAdjustments(priceRecipe, priceRecipeRanges, items, profilingRequest);
        }
    }

    /**
     * Groups the LineItems by their target dimension names based on the provided PriceRecipeRanges.
     * The LineItems are grouped if their total quantity of their source dimension names falls within the specified start and end tier range.
     *
     * @param lineItems         The list of LineItems to be grouped.
     * @param priceRecipeRanges The list of PriceRecipeRange objects defining the grouping criteria.
     * @return A map where the key is the target dimension name and the value is the list of LineItems that belong to that group.
     */
    private Map<String, List<LineItem>> groupLineItemsByDimension(List<LineItem> lineItems, List<PriceRecipeRange> priceRecipeRanges) {
        Map<String, List<LineItem>> groupedLineItems = new HashMap<>();

        // Iterate over each PriceRecipeRange to determine how to group LineItems
        for (PriceRecipeRange range : priceRecipeRanges) {
            // Get the source dimension names to group the LineItems
            List<String> sourceDimensionNames = range.getSourceDimensionName();

            // Group the LineItems by source dimension names
            Map<String, List<LineItem>> tempGroupedItems = filterLineItemsByDimensions(lineItems, sourceDimensionNames);

            // Process each group to determine if it meets the total quantity condition
            for (Map.Entry<String, List<LineItem>> entry : tempGroupedItems.entrySet()) {
                List<LineItem> itemsInGroup = entry.getValue();
                double totalQuantity = calculateTotalQuantity(itemsInGroup);

                // If the total quantity is within the specified tier range, group the items by the target dimension name
                if (isTotalQuantityInRange(totalQuantity, range)) {
                    groupedLineItems.putAll(filterLineItemsByDimensions(lineItems, range.getTargetDimensionName()));
                }
            }
        }

        return groupedLineItems;
    }

    /**
     * Filters LineItems based on the provided dimension names.
     *
     * @param lineItems            The list of LineItems to be filtered.
     * @param dimensionNames       The dimension names used for filtering.
     * @return A map where the key is the dimension name and the value is the list of LineItems that match that dimension.
     */
    private Map<String, List<LineItem>> filterLineItemsByDimensions(List<LineItem> lineItems, List<String> dimensionNames) {
        Map<String, List<LineItem>> filteredItems = new HashMap<>();

        for (LineItem lineItem : lineItems) {
            String timeDimensionName = lineItem.getTimeDimensionName();
            if (dimensionNames.contains(timeDimensionName)) {
                // Group the LineItems by the dimension name
                filteredItems.computeIfAbsent(timeDimensionName, k -> new ArrayList<>()).add(lineItem);
            }
        }

        return filteredItems;
    }

    /**
     * Determines if the total quantity is within the specified start and end tier range.
     *
     * @param totalQuantity The total quantity to check.
     * @param range         The PriceRecipeRange object defining the start and end tier limits.
     * @return true if the total quantity is within the range, otherwise false.
     */
    private boolean isTotalQuantityInRange(double totalQuantity, PriceRecipeRange range) {
        return totalQuantity >= range.getStartTier() && totalQuantity <= range.getEndTier();
    }


    /**
     * Applies price adjustments (discount/markup) to the grouped LineItems
     *
     * @param priceRecipe      The PriceRecipe containing price adjustment rules.
     * @param priceRecipeRanges List of PriceRecipeRange objects defining the grouping criteria.
     * @param items            The list of LineItems that belong to the current group.
     * @param profilingRequest  The request object containing discount details.
     */
    private void applyPriceAdjustments(PriceRecipe priceRecipe, List<PriceRecipeRange> priceRecipeRanges, List<LineItem> items, ProfilingRequestDTO profilingRequest) {
        // Check each PriceRecipeRange to see if it matches the quantity criteria
        for (PriceRecipeRange range : priceRecipeRanges) {
            applyAdjustmentsToLineItems(range, items, profilingRequest, priceRecipe);
        }
    }

    /**
     * Applies the price adjustments to each LineItem in the specified group.
     *
     * @param range             The PriceRecipeRange that defines the adjustment rules.
     * @param items             The list of LineItems to adjust.
     * @param profilingRequest   The request object containing discount details.
     * @param priceRecipe       The PriceRecipe containing the ID and price applied to.
     */
    private void applyAdjustmentsToLineItems(PriceRecipeRange range, List<LineItem> items, ProfilingRequestDTO profilingRequest, PriceRecipe priceRecipe) {
        for (LineItem item : items) {
            double adjustedPrice = calculateAdjustedPrice(
                    item.getNetPrice(), // need to correct this value for real case, Get the value from the field that AppliedTo has configured
                    range.getDealStrategy(),
                    range.getApplicationType(),
                    range.getApplicationValue()
            );
            int nextSequence = getNextDiscountSequence(profilingRequest);

            // Initialize discount details list if it's null
            if (profilingRequest.getDiscountDetails() == null) {
                profilingRequest.setDiscountDetails(new ArrayList<>());
            }

            // Add discount details to the profiling request
            profilingRequest.getDiscountDetails().add(createDiscountDetails(range, item, adjustedPrice, nextSequence, priceRecipe));
        }
    }

    /**
     * Retrieves the next sequence number for discount details,
     * returning 0 if no discount details exist.
     *
     * @param profilingRequest The request object containing discount details.
     * @return The next sequence number.
     */
    private int getNextDiscountSequence(ProfilingRequestDTO profilingRequest) {
        if (profilingRequest.getDiscountDetails() != null && !profilingRequest.getDiscountDetails().isEmpty()) {
            return profilingRequest.getDiscountDetails().getLast().getSequence() + 1;
        }
        return 0; // Default to 0 if no discount details exist
    }

    /**
     * Creates a DiscountDetails object from the given parameters.
     *
     * @param range         The PriceRecipeRange that defines the adjustment rules.
     * @param item          The LineItem being adjusted.
     * @param adjustedPrice The adjusted price after applying the discount or markup.
     * @param nextSequence  The next sequence number for discount details.
     * @param priceRecipe   The PriceRecipe containing the ID and price applied to.
     * @return A new DiscountDetails object.
     */
    private DiscountDetails createDiscountDetails(PriceRecipeRange range, LineItem item, double adjustedPrice, int nextSequence, PriceRecipe priceRecipe) {
        return new DiscountDetails(
                priceRecipe.getApplicationType(),
                priceRecipe.getApplicationValue(),
                Double.parseDouble(priceRecipe.getPriceApplicationON()),
                adjustedPrice,
                0d, // need to correct this value for real case
                new Date().getTime(), // need to correct this value for real case
                "Recipe / Formula", // need to correct this value for real case
                null, // need to correct this value for real case
                item.getProductId(),
                item.getId(),
                nextSequence,
                null, // need to correct this value for real case
                priceRecipe.getId(),
                priceRecipe.getPriceAppliedTo()
        );
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
     * Calculates the adjusted price based on the deal strategy and application type.
     *
     * @param price Get the value from the field that AppliedTo has configured
     * @param dealStrategy The strategy for price adjustment (discount or markup).
     * @param applicationType The type of adjustment (% or Amount).
     * @param applicationValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the discount or markup.
     */
    private double calculateAdjustedPrice(double price, String dealStrategy, String applicationType, String applicationValue) {
        double adjustmentValue = Double.parseDouble(applicationValue);

        // Apply discount or markup based on the dealStrategy
        return switch (dealStrategy.toLowerCase()) {
            case "discount" -> applyDiscount(price, applicationType, adjustmentValue);
            case "markup" -> applyMarkup(price, applicationType, adjustmentValue);
            default ->
                // If no valid dealStrategy is provided, return the original price
                    price;
        };
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
