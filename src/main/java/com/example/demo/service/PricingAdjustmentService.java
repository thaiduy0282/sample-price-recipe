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
        // Group LineItems by productId first
        Map<String, List<LineItem>> groupedByProducts = groupLineItemsByProductId(profilingRequest.getLineItems());

        // Iterate over each group of LineItems by productId
        for (Map.Entry<String, List<LineItem>> productEntry : groupedByProducts.entrySet()) {
            List<LineItem> productItems = productEntry.getValue();
            // Now group the LineItems by their timeDimensionName
            Map<String, List<LineItem>> groupedLineItems = groupLineItemsByDimension(productItems, priceRecipe.getRanges());

            // Iterate over each group of LineItems by timeDimensionName
            for (Map.Entry<String, List<LineItem>> entry : groupedLineItems.entrySet()) {
                String timeDimensionName = entry.getKey();
                List<LineItem> items = entry.getValue();
                List<PriceRecipeRange> priceRecipeRanges = priceRecipe.getRanges().stream()
                        .filter(r -> r.getTargetDimensionName().contains(timeDimensionName)).toList();

                // Apply price adjustments for groups
                applyPriceAdjustments(priceRecipe, priceRecipeRanges, items, profilingRequest);
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
     * @param priceRecipeRanges The list of PriceRecipeRange objects defining the grouping criteria.
     * @return A map where the key is the target dimension name and the value is the list of LineItems that belong to that group.
     */
    private Map<String, List<LineItem>> groupLineItemsByDimension(List<LineItem> lineItems, List<PriceRecipeRange> priceRecipeRanges) {
        Map<String, List<LineItem>> groupedLineItems = new HashMap<>();

        // Iterate over each PriceRecipeRange to determine how to group LineItems
        for (PriceRecipeRange range : priceRecipeRanges) {
            // Get the source dimension names to group the LineItems
            List<String> sourceDimensionNames = range.getSourceDimensionName();

            // Get the target dimension names to group the LineItems
            List<String> targetDimensionNames = range.getTargetDimensionName();

            if (sourceDimensionNames == null || targetDimensionNames == null) {
                continue;
            }

            // Group the LineItems by source dimension names
            Map<String, List<LineItem>> tempGroupedItems = filterLineItemsByDimensions(lineItems, sourceDimensionNames);

            // Process each group to determine if it meets the total quantity condition
            for (Map.Entry<String, List<LineItem>> entry : tempGroupedItems.entrySet()) {
                List<LineItem> itemsInGroup = entry.getValue();
                double totalQuantity = calculateTotalQuantity(itemsInGroup);

                // If the total quantity is within the specified tier range, group the items by the target dimension name
                if (isTotalQuantityInRange(totalQuantity, range)) {
                    groupedLineItems.putAll(filterLineItemsByDimensions(lineItems, targetDimensionNames));
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
     * Applies price adjustments to the provided list of LineItems based on the given PriceRecipeRange and profiling request details.
     *
     * This method ensures that each LineItem is evaluated for applicable discounts, calculates the adjusted price, and adds
     * a new DiscountDetails entry to the profiling request.
     *
     * @param priceRecipeRange    The PriceRecipeRange that defines the deal strategy, application type, and value.
     * @param lineItems           The list of LineItems that require adjustments.
     * @param profilingRequest    The ProfilingRequestDTO containing the discount details for the request.
     * @param priceRecipe         The PriceRecipe object which holds the rules for pricing application.
     */
    private void applyAdjustmentsToLineItems(PriceRecipeRange priceRecipeRange, List<LineItem> lineItems, ProfilingRequestDTO profilingRequest, PriceRecipe priceRecipe) {
        // Initialize the discount details list if it's null
        if (profilingRequest.getDiscountDetails() == null) {
            profilingRequest.setDiscountDetails(new ArrayList<>());
        }

        // Loop through each LineItem to evaluate discounts and apply adjustments
        for (LineItem lineItem : lineItems) {

            // Get the latest discount detail for this LineItem and pricing context
            DiscountDetails latestDiscount = findLatestDiscountDetail(lineItem.getId(), priceRecipe.getPriceApplicationON(), profilingRequest);

            // Only apply adjustments if there are existing discounts
            if (latestDiscount != null) {

                // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                double adjustedPrice = calculateAdjustedPrice(
                        latestDiscount.getAfterAdjustment(),
                        priceRecipeRange.getDealStrategy(),
                        priceRecipeRange.getApplicationType(),
                        priceRecipeRange.getApplicationValue()
                );

                // Determine the next sequence number for the discount
                int nextSequence = getNextDiscountSequence(profilingRequest);

                // Create and add a new DiscountDetails entry to the profiling request
                List<DiscountDetails> discountDetails = new ArrayList<>(profilingRequest.getDiscountDetails());
                discountDetails.add(createDiscountDetails(lineItem, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe));
                profilingRequest.setDiscountDetails(discountDetails);
            }
        }
    }

    /**
     * Finds the latest DiscountDetail for the given LineItem ID and applied pricing context.
     *
     * @param lineItemId The ID of the LineItem to find the discount for.
     * @param priceApplicationON The pricing context in which the discount is applied.
     * @param profilingRequest The profiling request containing existing discount details.
     * @return The latest DiscountDetail for the LineItem, or null if no discount exists.
     */
    private DiscountDetails findLatestDiscountDetail(String lineItemId, String priceApplicationON, ProfilingRequestDTO profilingRequest) {
        List<DiscountDetails> applicableDiscounts = new ArrayList<>(profilingRequest.getDiscountDetails().stream()
                .filter(discountDetail -> discountDetail.getLineItemId().equals(lineItemId)
                        && discountDetail.getName().equals(priceApplicationON))
                .toList());

        applicableDiscounts.sort(Comparator.comparingInt(DiscountDetails::getSequence));

        // Return the last (latest) DiscountDetail, or null if the list is empty
        return applicableDiscounts.isEmpty() ? null : applicableDiscounts.get(applicableDiscounts.size() - 1);
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
            return profilingRequest.getDiscountDetails().get(profilingRequest.getDiscountDetails().size() - 1).getSequence() + 1;
        }
        return 0; // Default to 0 if no discount details exist
    }

    /**
     * Creates a DiscountDetails object based on the provided line item and pricing information.
     *
     * @param lineItem The line item for which discount details are being created.
     * @param adjustedPrice The adjusted price calculated after applying the discount or markup.
     * @param sequenceNumber The sequence number for the discount details, indicating its order.
     * @param priceRecipe The price recipe containing details about the pricing application.
     * @return A DiscountDetails object populated with the relevant information.
     */
    private DiscountDetails createDiscountDetails(LineItem lineItem, double beforeAdjustmentPrice, double adjustedPrice, int sequenceNumber, PriceRecipe priceRecipe) {
        // Create a new DiscountDetails object
        DiscountDetails discountDetails = new DiscountDetails(
                priceRecipe.getApplicationType(), // Type of application (Discount/Markup)
                priceRecipe.getApplicationValue(), // Value applied (e.g., percentage or amount)
                beforeAdjustmentPrice, // Original price before adjustment
                adjustedPrice, // The calculated adjusted price after applying discount or markup
                0d, // Placeholder for an unspecified value; to be corrected for the real case
                new Date().getTime(), // Timestamp of the discount application; to be corrected for the real case
                "Recipe", // Placeholder for a more descriptive name; to be corrected for the real case
                null, // Placeholder for a reference; to be corrected for the real case
                lineItem.getProductId(), // The product ID from the line item
                lineItem.getId(), // The line item ID
                sequenceNumber, // Sequence number to determine the order of discount details
                null, // Placeholder for additional information; to be corrected for the real case
                priceRecipe.getId(), // ID of the price recipe
                priceRecipe.getPriceAppliedTo() // The target of the price application (e.g., product or service)
        );

        // Set the name of the discount details based on the price application's original price
        discountDetails.setName(priceRecipe.getPriceAppliedTo());

        return discountDetails; // Return the populated DiscountDetails object
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
     * Applies a discount to the price based on the application type.
     *
     * @param price The original price of the item.
     * @param applicationType The type of adjustment (% or Amount).
     * @param adjustmentValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the discount.
     */
    private double applyDiscount(double price, String applicationType, double adjustmentValue) {
        if ("Percentage".equalsIgnoreCase(applicationType)) {
            // Apply percentage-based discount
            return price - (price * (adjustmentValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            // Apply fixed amount discount
            return price - adjustmentValue;
        }
        // Return the original netPrice if no valid applicationType is provided
        return price;
    }

    /**
     * Applies a markup to the price based on the application type.
     *
     * @param price The original price of the item.
     * @param applicationType The type of adjustment (% or Amount).
     * @param adjustmentValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the markup.
     */
    private double applyMarkup(double price, String applicationType, double adjustmentValue) {
        if ("Percentage".equalsIgnoreCase(applicationType)) {
            // Apply percentage-based markup
            return price + (price * (adjustmentValue / 100));
        } else if ("Amount".equalsIgnoreCase(applicationType)) {
            // Apply fixed amount markup
            return price + adjustmentValue;
        }
        // Return the original netPrice if no valid applicationType is provided
        return price;
    }
}
