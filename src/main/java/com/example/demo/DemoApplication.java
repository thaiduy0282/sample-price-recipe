package com.example.demo;

import com.example.demo.models.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.demo.utils.MockDataGenerator.fetchAllRecipes;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		//The calculateCumulativeRange function has been written with unit tests. Please refer to how to use it.
	}


	private void executeVolume(List<PriceProfileStep> steps, ProfilingRequestDTO profilingRequestDTO, Map<String, List<PriceListItem>> priceListItemMap, Map<String, List<PriceList>> priceListById ) {
		// fetching all the recipes
		List<PriceRecipe> recipes = fetchAllRecipes();

		// sorting the priceProfileStep base on the sequence field
		steps.sort(Comparator.comparingInt(PriceProfileStep::getSequence));
		for (PriceProfileStep step : steps) {
			if (Objects.equals(step.getPricingMethod(), "FORMULA")) {
				// handling the formula
			} else if (Objects.equals(step.getPricingMethod(), "RECIPE")) {
				// filtering the recipe from the recipes list based scope/scopeValue/priceSettings
                List<PriceRecipe> matchingRecipes = recipes.stream().filter(r -> Objects.equals(r.getScope(), step.getScope())
										&& Objects.equals(r.getScopeValue(), step.getScopeValue())
										&& Objects.equals(r.getPriceSetting(), step.getPriceSetting())).toList();

				// apply the recipe logic
                for (PriceRecipe recipe : matchingRecipes) {
					if (recipe.getPriceSetting().equals("DealMax")) {
						// Checking for the type of the PriceSetting
						if (recipe.getType().equals("BuyXGetY")) {
							calculateByXGetY(recipe, profilingRequestDTO);
						} else {
							// handle for other type in the else clause as well
						}
					} else {
						// handle for other priceSettings in the else clause as well
					}
				}
			}
		}
	}

	private void calculateByXGetY(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {
		// handle logic for calculating

		// Create the DiscountDetails object with the sequence number base on the sequence fo the previous object

		// Append the DiscountDetails object to the profileRequestDTO object
	}

	/**
	 * This method calculates cumulative ranges for each group of LineItems based on
	 * their timeDimensionName. If the timeDimensionName matches the sourceDimensionName
	 * in the PriceRecipeRange and the total quantity falls within the range of startTier
	 * and endTier, then adjustments (discount/markup) are applied to the netPrice.
	 *
	 * @param recipe The PriceRecipe containing multiple PriceRecipeRanges with pricing strategies.
	 * @param profilingRequestDTO The request object containing a list of LineItems to be processed.
	 */
    void calculateCumulativeRange(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO) {

		// Group LineItems by their timeDimensionName
		Map<String, List<LineItem>> groupedByTimeDimension = groupLineItemsByTimeDimension(profilingRequestDTO.getLineItems());

		// Iterate over each group of LineItems based on timeDimensionName
		for (Map.Entry<String, List<LineItem>> entry : groupedByTimeDimension.entrySet()) {
			String timeDimensionName = entry.getKey();
			List<LineItem> items = entry.getValue();

			// Apply price adjustments for groups that satisfy the conditions
			applyPriceAdjustments(recipe.getRanges(), timeDimensionName, items);
		}
	}

	/**
	 * Groups the provided list of LineItems by their timeDimensionName.
	 *
	 * @param lineItems List of LineItems to be grouped.
	 * @return A map where the key is the timeDimensionName, and the value is the list of LineItems that belong to that group.
	 */
	private Map<String, List<LineItem>> groupLineItemsByTimeDimension(List<LineItem> lineItems) {
		// Group LineItems by timeDimensionName
		return lineItems.stream()
				.collect(Collectors.groupingBy(LineItem::getTimeDimensionName));
	}

	/**
	 * Applies price adjustments (discount/markup) to the grouped LineItems if the group meets
	 * the specified conditions based on the PriceRecipeRange.
	 *
	 * @param priceRecipeRanges List of PriceRecipeRange objects that define the price adjustment rules.
	 * @param timeDimensionName The name of the time dimension for the current group of LineItems.
	 * @param items The list of LineItems that belong to the current group.
	 */
	private void applyPriceAdjustments(List<PriceRecipeRange> priceRecipeRanges, String timeDimensionName, List<LineItem> items) {

		// Iterate over each PriceRecipeRange to check if the timeDimensionName and quantity match the conditions
		for (PriceRecipeRange range : priceRecipeRanges) {

			// Check if timeDimensionName matches the sourceDimensionName of the current PriceRecipeRange
			if (range.getSourceDimensionName().contains(timeDimensionName)) {

				// Calculate the total quantity for the current group
				double totalQuantity = calculateTotalQuantity(items);

				// Check if the total quantity falls within the defined startTier and endTier
				if (isQuantityInRange(totalQuantity, range)) {
					// Apply price adjustments based on dealStrategy and applicationType
					adjustPricesForMatchingItems(range, items);
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
	 * Adjusts the prices for the LineItems based on the dealStrategy (discount or markup)
	 * and applicationType (% or Amount) defined in the PriceRecipeRange.
	 *
	 * @param range The PriceRecipeRange that defines how the prices should be adjusted.
	 * @param items The list of LineItems to be adjusted.
	 */
	private void adjustPricesForMatchingItems(PriceRecipeRange range, List<LineItem> items) {

		// Iterate over each LineItem and apply the price adjustment if the targetDimensionName matches
		for (LineItem item : items) {
			if (range.getTargetDimensionName().contains(item.getTimeDimensionName())) {
				// Adjust the netPrice based on the dealStrategy and applicationType
				double adjustedPrice = calculateAdjustedPrice(item.getNetPrice(), range.getDealStrategy(), range.getApplicationType(), range.getApplicationValue());

				// Update the netPrice for the LineItem
				item.setNetPrice(adjustedPrice);

				// Log or output the updated price for validation
				System.out.println("Updated Net Price for " + item.getTimeDimensionName() + ": " + adjustedPrice);
			}
		}
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
				if ("%".equalsIgnoreCase(applicationType)) {
					// Apply percentage-based discount
					return netPrice - (netPrice * (adjustmentValue / 100));
				} else if ("Amount".equalsIgnoreCase(applicationType)) {
					// Apply fixed amount discount
					return netPrice - adjustmentValue;
				}
				break;

			case "markup":
				if ("%".equalsIgnoreCase(applicationType)) {
					// Apply percentage-based markup
					return netPrice + (netPrice * (adjustmentValue / 100));
				} else if ("Amount".equalsIgnoreCase(applicationType)) {
					// Apply fixed amount markup
					return netPrice + adjustmentValue;
				}
				break;
		}
		// Return the original netPrice if no adjustment is applied
		return netPrice;
	}
}
