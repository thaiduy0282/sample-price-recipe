package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.*;

import java.util.List;

public class BuyXGetYService {

    public void calculatePriceOneOff(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        // Check if the buy conditions are satisfied
        boolean canApplyReward = evaluateBuyConditions(recipe.getBuySection().getBuyConditions(), lineItems);

        if (canApplyReward) {
            // Apply the rewards from the GetSection
            applyRewards(recipe.getGetSection().getGetRewards(), recipe, profilingRequestDTO);
        }
    }

    private boolean evaluateBuyConditions(List<BuyCondition> buyConditions, List<LineItem> lineItems) {
        for (BuyCondition condition : buyConditions) {
            switch (condition.getConditionType()) {
                case "buyItem": //if a specific item is purchased
                    if (!checkItemPurchase(condition.getItemId(), lineItems)) {
                        return false;
                    }
                    break;

                case "buyMoreThanPrice": //if the total price exceeds a threshold
                    if (!checkTotalPrice(condition.getThresholdAmount(), lineItems)) {
                        return false;
                    }
                    break;

                case "buyMoreThanQuantity": //if more than a certain quantity of an item is bought
                    if (!checkQuantity(condition.getItemId(), condition.getQuantity(), lineItems)) {
                        return false;
                    }
                    break;

                case "buyDifferentItems": //if a combination of different items is purchased
                    if (!checkDifferentItems(condition.getDifferentItemIds(), lineItems)) {
                        return false;
                    }
                    break;

                default:
                    return false;
            }
        }
        return true; // All conditions are met
    }

    private boolean checkItemPurchase(String itemId, List<LineItem> lineItems) {
        return lineItems.stream().anyMatch(item -> item.getProductId().equals(itemId));
    }

    private boolean checkTotalPrice(double thresholdAmount, List<LineItem> lineItems) {
        double totalPrice = lineItems.stream().mapToDouble(LineItem::getNetPrice).sum();
        return totalPrice >= thresholdAmount;
    }

    private boolean checkQuantity(String itemId, int thresholdQuantity, List<LineItem> lineItems) {
        return lineItems.stream()
                .filter(item -> item.getProductId().equals(itemId))
                .mapToDouble(LineItem::getQuantity)
                .sum() >= thresholdQuantity;
    }

    private boolean checkDifferentItems(List<String> differentItemIds, List<LineItem> lineItems) {
        long count = lineItems.stream()
                .filter(item -> differentItemIds.contains(item.getProductId()))
                .count();
        return count >= differentItemIds.size();
    }

    private void applyRewards(List<GetReward> getRewards, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        for (GetReward reward : getRewards) {
            switch (reward.getRewardType()) {
                case "discountPercentage":
                    applyDiscountPercentage(reward, priceRecipe, profilingRequestDTO);
                    break;

                case "discountAmount":
                    applyDiscountAmount(reward, priceRecipe, profilingRequestDTO);
                    break;

                case "freeItem":
                    addFreeItem(reward, priceRecipe, profilingRequestDTO);
                    break;

                case "freeShipping":
                    applyFreeShipping(priceRecipe, profilingRequestDTO);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * percentage discounts
     *
     */
    private void applyDiscountPercentage(GetReward reward, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        double discount = 0.0;

        if (reward.isApplyToTotal()) {
            double totalAmount = lineItems.stream().mapToDouble(LineItem::getNetPrice).sum();
            discount = totalAmount * reward.getDiscountPercentage() / 100;
            discount = Math.min(discount, reward.getMaxDiscount());
        } else {
            for (LineItem lineItem : lineItems) {
                if (lineItem.getProductId().equals(reward.getRewardItemId())) {
                    discount = lineItem.getNetPrice() * reward.getDiscountPercentage() / 100;
                    discount = Math.min(discount, reward.getMaxDiscount());
                    break;
                }
                // Get the latest discount detail for this LineItem and pricing context
                DiscountDetails latestDiscount = Util.findLatestDiscountDetail(lineItem.getId(), priceRecipe.getPriceApplicationON(), profilingRequestDTO);

                // Only apply adjustments if there are existing discounts
                if (latestDiscount != null) {

                    // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                    double adjustedPrice = Util.calculateAdjustedPrice(
                            latestDiscount.getAfterAdjustment(),
                            priceRecipe.getDealStrategy(), // Discount
                            priceRecipe.getApplicationType(), // Percent
                            String.valueOf(discount)
                    );

                    // Determine the next sequence number for the discount
                    int nextSequence = latestDiscount.getSequence() + 1;

                    // Create and add a new DiscountDetails entry to the profiling request
                    Util.createAndAddDiscountDetails(lineItem, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequestDTO);

                }
            }
        }

        DiscountDetails discountDetail = new DiscountDetails();
        discountDetail.setAdjustmentType("Discount Percentage");
        discountDetail.setAdjustmentValue(discount);
        profilingRequestDTO.getDiscountDetails().add(discountDetail);
    }

    /**
     * fixed discount amounts
     *
     */
    private void applyDiscountAmount(GetReward reward, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        double discount = reward.getDiscountAmount();

        for (LineItem lineItem : lineItems) {
            // Get the latest discount detail for this LineItem and pricing context
            DiscountDetails latestDiscount = Util.findLatestDiscountDetail(lineItem.getId(), priceRecipe.getPriceApplicationON(), profilingRequestDTO);

            // Only apply adjustments if there are existing discounts
            if (latestDiscount != null) {

                // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                double adjustedPrice = Util.calculateAdjustedPrice(
                        latestDiscount.getAfterAdjustment(),
                        priceRecipe.getDealStrategy(), // Discount
                        priceRecipe.getApplicationType(), // Percent
                        String.valueOf(discount)
                );

                // Determine the next sequence number for the discount
                int nextSequence = latestDiscount.getSequence() + 1;

                // Create and add a new DiscountDetails entry to the profiling request
                // AdjustmentType("Discount Amount");
                Util.createAndAddDiscountDetails(lineItem, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequestDTO);

            }
        }
    }

    /**
     * Get free items
     *
     */
    private void addFreeItem(GetReward reward, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        for (int i = 0; i < reward.getFreeItemQuantity(); i++) {
            LineItem freeItem = new LineItem();
            freeItem.setProductId(reward.getRewardItemId());
            freeItem.setNetPrice(0.0); // Free item
            freeItem.setQuantity(1.0);
            profilingRequestDTO.getLineItems().add(freeItem);
        }
    }

    /**
     * free shipping
     *
     */
    private void applyFreeShipping(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        DiscountDetails discountDetail = new DiscountDetails();
        discountDetail.setAdjustmentType("Free Shipping");
        discountDetail.setAdjustmentValue(0.0); // Free shipping, so no cost
        profilingRequestDTO.getDiscountDetails().add(discountDetail);
    }
}
