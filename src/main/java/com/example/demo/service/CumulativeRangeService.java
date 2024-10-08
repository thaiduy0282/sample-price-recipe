package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.*;

import java.util.*;


/**
 * This class is responsible for processing pricing adjustments for LineItems based on specific rules defined in PriceRecipe.
 */
public class CumulativeRangeService {
    /**
     * Calculates cumulative price adjustments for each group of LineItems based on their timeDimensionName.
     * If the timeDimensionName matches the sourceDimensionName in the PriceRecipeRange and the total quantity
     * falls within the startTier and endTier, then adjustments (discount/markup) are applied to the netPrice.
     *
     * @param priceRecipe The PriceRecipe containing multiple PriceRecipeRanges with pricing strategies.
     * @param profilingRequest The request object containing a list of LineItems to be processed.
     */
    public void calculateCumulativeRange(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        // Group LineItems by productId first
        Map<String, List<LineItem>> groupedByProducts = groupLineItemsByProductId(profilingRequest.getLineItems());

        // Iterate over each group of LineItems by productId
        for (Map.Entry<String, List<LineItem>> productEntry : groupedByProducts.entrySet()) {
            List<LineItem> productItems = productEntry.getValue();

            // Focus on the last range only
            PriceRecipeRange recipeRange = priceRecipe.getRanges().get(priceRecipe.getRanges().size() - 1);

            // Now group the LineItems by their timeDimensionName
            Map<String, List<LineItem>> groupedLineItems = groupLineItemsByDimension(productItems, recipeRange);

            // Iterate over each group of LineItems by timeDimensionName
            for (Map.Entry<String, List<LineItem>> entry : groupedLineItems.entrySet()) {
                List<LineItem> items = entry.getValue();

                applyAdjustmentsToLineItems(recipeRange, items, profilingRequest, priceRecipe);
            }
        }
    }

    /**
     * Groups LineItems by their productId.
     *
     * @param lineItems The list of LineItems to be grouped.
     * @return A map where the key is the productId and the value is the list of LineItems that belong to that group.
     */
    private Map<String, List<LineItem>> groupLineItemsByProductId(List<LineItem> lineItems) {
        Map<String, List<LineItem>> groupedLineItems = new HashMap<>();

        for (LineItem lineItem : lineItems) {
            String productId = lineItem.getProductId();
            groupedLineItems.computeIfAbsent(productId, k -> new ArrayList<>()).add(lineItem);
        }

        return groupedLineItems;
    }

    /**
     * Groups the LineItems by their target dimension names based on the provided PriceRecipeRanges.
     * The LineItems are grouped if their total quantity of their source dimension names falls within the specified start and end tier range.
     *
     * @param lineItems         The list of LineItems to be grouped.
     * @param priceRecipeRange PriceRecipeRange objects defining the grouping criteria.
     * @return A map where the key is the target dimension name and the value is the list of LineItems that belong to that group.
     */
    private Map<String, List<LineItem>> groupLineItemsByDimension(List<LineItem> lineItems, PriceRecipeRange priceRecipeRange) {
        Map<String, List<LineItem>> groupedLineItems = new HashMap<>();

        // Get the source dimension names to group the LineItems
        List<String> sourceDimensionNames = priceRecipeRange.getSourceDimensionName();

        // Get the target dimension names to group the LineItems
        List<String> targetDimensionNames = priceRecipeRange.getTargetDimensionName();

        if (sourceDimensionNames == null || targetDimensionNames == null) {
            return groupedLineItems;
        }

        // Group the LineItems by source dimension names
        Map<String, List<LineItem>> tempGroupedItems = filterLineItemsByDimensions(lineItems, sourceDimensionNames);

        // Process each group to determine if it meets the total quantity condition
        for (Map.Entry<String, List<LineItem>> entry : tempGroupedItems.entrySet()) {
            List<LineItem> itemsInGroup = entry.getValue();
            double totalQuantity = calculateTotalQuantity(itemsInGroup);

            // If the total quantity is within the specified tier range, group the items by the target dimension name
            if (isTotalQuantityInRange(totalQuantity, priceRecipeRange)) {
                groupedLineItems.putAll(filterLineItemsByDimensions(lineItems, targetDimensionNames));
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
     * Applies adjustments to the provided LineItems based on the PriceRecipeRange and updates the ProfilingRequestDTO
     * with new DiscountDetails entries.
     *
     * @param priceRecipeRange  The range defining how discounts should be applied.
     * @param lineItems         A list of LineItem objects representing the items being processed.
     * @param profilingRequest  The profiling request data object where discount details are stored.
     * @param priceRecipe       The price recipe containing discount application details.
     */
    private void applyAdjustmentsToLineItems(PriceRecipeRange priceRecipeRange, List<LineItem> lineItems, ProfilingRequestDTO profilingRequest, PriceRecipe priceRecipe) {
        // Initialize the discount details list if it's null
        if (profilingRequest.getDiscountDetails() == null) {
            profilingRequest.setDiscountDetails(new ArrayList<>());
        }

        // Loop through each LineItem to evaluate discounts and apply adjustments
        for (LineItem lineItem : lineItems) {

            // Get the latest discount detail for this LineItem and pricing context
            DiscountDetails latestDiscount = Util.findLatestDiscountDetail(lineItem.getId(), priceRecipe.getPriceApplicationON(), profilingRequest);

            // Only apply adjustments if there are existing discounts
            if (latestDiscount != null) {

                // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                double adjustedPrice = Util.calculateAdjustedPrice(
                        latestDiscount.getAfterAdjustment(),
                        priceRecipeRange.getDealStrategy(),
                        priceRecipeRange.getApplicationType(),
                        priceRecipeRange.getApplicationValue()
                );

                // Determine the next sequence number for the discount
                int nextSequence = latestDiscount.getSequence() + 1;

                // Create and add a new DiscountDetails entry to the profiling request
                Util.createAndAddDiscountDetails(lineItem, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequest);
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
}
