package com.example.demo;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.PriceRecipeRange;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.models.buyXGetY.Adjustment;
import com.example.demo.models.buyXGetY.BuyConditionGroup;
import com.example.demo.models.buyXGetY.Condition;
import com.example.demo.utils.FormulaEvaluator;
import lombok.val;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String applicationType = ranges == null || ranges.isEmpty() ? priceRecipe.getApplicationType() : ranges.get(ranges.size() - 1).getApplicationType();
        Double applicationValue = ranges == null || ranges.isEmpty() ? priceRecipe.getApplicationValue() : Double.parseDouble(ranges.get(ranges.size() - 1).getApplicationValue());

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

    /**
     * Calculates the adjusted price based on the deal strategy and application type.
     *
     * @param price Get the value from the field that AppliedTo has configured
     * @param dealStrategy The strategy for price adjustment (discount or markup).
     * @param applicationType The type of adjustment (% or Amount).
     * @param applicationValue The value of the adjustment (either a percentage or an amount).
     * @return The adjusted price after applying the discount or markup.
     */
    public static double calculateAdjustedPrice(double price, String dealStrategy, String applicationType, String applicationValue) {
        double adjustmentValue = Double.parseDouble(applicationValue);

        // Apply discount or markup based on the dealStrategy
        return switch (dealStrategy.toLowerCase()) {
            case "discount" -> applyDiscount(price, applicationType, adjustmentValue);
            case "markup" -> applyMarkup(price, applicationType, adjustmentValue);
            case "updatevalue" -> adjustmentValue;
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
    private static double applyDiscount(double price, String applicationType, double adjustmentValue) {
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
    private static double applyMarkup(double price, String applicationType, double adjustmentValue) {
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

    public static boolean isValidFormula(String formula, LineItem lineItem) {
        return !StringUtils.hasLength(formula) ||
                FormulaEvaluator.evaluateFormula(formula, lineItem);
    }

    public static double calculateAdjustedPriceWithLimit(Double price, String dealStrategy, String applicationType, double maxAdjustValue, double applicationValue) {
        double adjustedPrice = price;
        double adjustmentValue = getAdjustmentValue(price, applicationType, maxAdjustValue, applicationValue);

        if ("discount".equalsIgnoreCase(dealStrategy)) {
            adjustedPrice = Math.max(0, price - adjustmentValue);
        } else if ("markup".equalsIgnoreCase(dealStrategy)) {
            adjustedPrice = price + adjustmentValue;
        }

        return  adjustedPrice;
    }

    private static double getAdjustmentValue(Double price, String applicationType, double maxAdjustValue, double applicationValue) {
        double adjustmentValue;

        switch (applicationType) {
            case "Percentage" -> // Apply percentage-based
                    adjustmentValue = (price * (applicationValue / 100));
            case "Amount" -> // Apply fixed amount-based
                    adjustmentValue = applicationValue;
            default -> throw new IllegalStateException("Unexpected value: " + applicationType);
        }

        if (maxAdjustValue != 0) {
            adjustmentValue = Math.min(adjustmentValue, maxAdjustValue);
        }
        return adjustmentValue;
    }

    public static boolean matchesCondition(Condition condition, List<LineItem> item) {
        return item.stream()
            .anyMatch(i ->
                i.getProductId().equals(condition.getObjectId())
                    && i.getQuantity().intValue() >= condition.getValue()
            );
    }

    public static int maxApplicableTimes(List<LineItem> lineItems, Condition condition) {
        return lineItems.stream()
            .filter(item -> item.getProductId().equals(condition.getObjectId()))
            .mapToInt(item -> (int) (item.getQuantity() / condition.getValue()))
            .findFirst()
            .orElse(0);
    }

    public static Map<Adjustment, Double> createAdjustment(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO, BuyConditionGroup buyConditionGroup, int times) {
        // TODO: Just calculate based on first adjustment for now
        Adjustment adjustment = buyConditionGroup.getGetSection().getAdjustments().getFirst();
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        LineItem lineItem = lineItems.stream().filter(item -> item.getProductId().equals(adjustment.getProductId())).findFirst().orElse(null);

        if (lineItem == null) {
            return Collections.emptyMap();
        }

        // Get the latest discount detail for this LineItem and pricing context
        DiscountDetails latestDiscount = null;
        try {
            latestDiscount = findLatestDiscountDetail(lineItem.getId(), priceRecipe.getPriceApplicationON(), profilingRequestDTO);
        } catch (Exception e) {
            // handle log for exception or just ignore it
        }

        val priceAfterAdjustment = latestDiscount != null ? latestDiscount.getAfterAdjustment() : lineItem.getNetPrice();
        if (times > 1) {
            adjustment.setValue(adjustment.getValue() * times);
        }
        // Calculate the adjustmentValue
        double adjustmentValue = getAdjustmentValue(priceAfterAdjustment,
            priceRecipe.getApplicationType(), // Percent or Amount
            adjustment.getMaxAdjustmentAmount(), // Max adjustment
            adjustment.getValue());

        Map<Adjustment, Double> adjustmentMap = new HashMap<>();
        adjustmentMap.put(adjustment, adjustmentValue);

        return adjustmentMap;
    }
}
