package com.example.demo;

import com.example.demo.models.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Util {

    /**
     * Finds the latest DiscountDetail for the given LineItem ID and applied pricing context.
     *
     * @param lineItemId The ID of the LineItem to find the discount for.
     * @param priceApplicationON The pricing context in which the discount is applied.
     * @param profilingRequest The profiling request containing existing discount details.
     * @return The latest DiscountDetail for the LineItem, or null if no discount exists.
     */
    public static DiscountDetails findLatestDiscountDetail(String lineItemId, String priceApplicationON, ProfilingRequestDTO profilingRequest) {
        List<DiscountDetails> applicableDiscounts = new ArrayList<>(profilingRequest.getDiscountDetails().stream()
                .filter(discountDetail -> discountDetail.getLineItemId().equals(lineItemId)
                        && discountDetail.getName().equals(priceApplicationON))
                .toList());

        applicableDiscounts.sort(Comparator.comparingInt(DiscountDetails::getSequence));

        // Return the last (latest) DiscountDetail, or null if the list is empty
        return applicableDiscounts.isEmpty() ? null : applicableDiscounts.get(applicableDiscounts.size() - 1);
    }

    /**
     * Creates a new DiscountDetails entry based on the provided line item, latest discount, adjusted price, and sequence,
     * and adds it to the ProfilingRequestDTO's discount details list.
     *
     * @param lineItem          The current LineItem for which discount adjustments are being applied.
     * @param beforeAdjustmentPrice   Original price before adjustment.
     * @param adjustedPrice     The newly calculated adjusted price based on the price recipe range.
     * @param sequenceNumber      The sequence number for the new DiscountDetails entry.
     * @param priceRecipe       The PriceRecipe object containing discount application details.
     * @param profilingRequest  The ProfilingRequestDTO where the DiscountDetails entry will be added.
     */
    public static void createAndAddDiscountDetails(LineItem lineItem, double beforeAdjustmentPrice, double adjustedPrice, int sequenceNumber, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        // Create a new DiscountDetails object with the necessary parameters
        DiscountDetails discountDetails = createDiscountDetails(lineItem, beforeAdjustmentPrice, adjustedPrice, sequenceNumber, priceRecipe);

        // Add the newly created discount details to the profiling request's discount list
        List<DiscountDetails> discountList = new ArrayList<>(profilingRequest.getDiscountDetails());
        discountList.add(discountDetails);
        profilingRequest.setDiscountDetails(discountList);
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
    private static DiscountDetails createDiscountDetails(LineItem lineItem, double beforeAdjustmentPrice, double adjustedPrice, int sequenceNumber, PriceRecipe priceRecipe) {
        // Get the adjustment details
        List<PriceRecipeRange> ranges = priceRecipe.getRanges();
        String applicationType = ranges.isEmpty() ? priceRecipe.getApplicationType() : ranges.get(ranges.size() - 1).getApplicationType();
        Double applicationValue = ranges.isEmpty() ? priceRecipe.getApplicationValue() : Double.parseDouble(ranges.get(ranges.size() - 1).getApplicationValue());

        // Create a new DiscountDetails object
        DiscountDetails discountDetails = new DiscountDetails(
                applicationType, // Type of application (Discount/Markup)
                applicationValue, // Value applied (e.g., percentage or amount)
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
}
