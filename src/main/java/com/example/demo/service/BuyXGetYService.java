package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.common.LogicalOperator;
import com.example.demo.models.BuyCondition;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.models.Reward;

import java.util.ArrayList;
import java.util.List;

public class BuyXGetYService {

    public void calculatePriceOneOff(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        // Check if the buy conditions are satisfied
        boolean canApplyReward = evaluateBuyConditions(recipe.getBuySection().getBuyConditions(), lineItems);

        if (canApplyReward) {
            // Apply the rewards from the GetSection
            applyRewards(recipe.getGetSection().getRewards(), recipe, profilingRequestDTO);
        }
    }

    private boolean evaluateBuyConditions(List<BuyCondition> buyConditions, List<LineItem> lineItems) {
        boolean result = false;

        for (BuyCondition condition : buyConditions) {
            boolean conditionResult = evaluateConditionFormula(condition.getExpression(), lineItems);

            // Combine with logical operator (AND/OR)
            if (LogicalOperator.AND == condition.getLogicalOperator()) {
                result = result && conditionResult;
            } else if (LogicalOperator.OR == condition.getLogicalOperator()) {
                result = result || conditionResult;
            } else {
                // For the first condition
                result = conditionResult;
            }
        }
        return result;
    }

    private boolean evaluateConditionFormula(String expression, List<LineItem> lineItems) {
        for (LineItem item : lineItems) {
            // Extract the metadata and value to evaluate (e.g., "product.name=abc")
            var result = Util.isValidFormula(expression, item);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private void applyRewards(List<Reward> rewards, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        List<LineItem> lineItems = profilingRequestDTO.getLineItems();
        List<Reward> availableRewards = new ArrayList<>(rewards);  // Copy of rewards to modify


        for (LineItem item : lineItems) {
            for (Reward reward : availableRewards) {
                boolean rewardConditionMet = Util.isValidFormula(reward.getRewardExpression(), item);

                if (rewardConditionMet) {
                    applyReward(item, reward, priceRecipe, profilingRequestDTO);

                    // Remove the reward from the available list after applying it to one line item
                    availableRewards.remove(reward);
                    break;  // Exit the loop since the reward is applied
                }
            }
        }
    }

    /**
     * fixed discount amounts
     *
     */
    private void applyReward(LineItem item, Reward reward, PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequestDTO) {
        // Get the latest discount detail for this LineItem and pricing context
        DiscountDetails latestDiscount = Util.findLatestDiscountDetail(item.getId(), priceRecipe.getPriceApplicationON(), profilingRequestDTO);

        // Only apply adjustments if there are existing discounts
        if (latestDiscount != null) {

            // Calculate the adjusted price using the deal strategy and application details
            double adjustedPrice = Util.calculateAdjustedPriceWithLimit(
                    latestDiscount.getAfterAdjustment(),
                    priceRecipe.getDealStrategy(), // Discount
                    reward.getRewardType(), // Percent or Amount
                    reward.getMaxDiscount(), // Max discount
                    reward.getRewardValue()
            );

            // Determine the next sequence number for the discount
            int nextSequence = latestDiscount.getSequence() + 1;

            // Create and add a new DiscountDetails entry to the profiling request
            Util.createAndAddDiscountDetails(item, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequestDTO);
        }
    }

}
