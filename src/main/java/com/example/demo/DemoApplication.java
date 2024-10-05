package com.example.demo;

import com.example.demo.models.*;
import com.example.demo.utils.ParseFormula;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

import static com.example.demo.utils.MockDataGenerator.fetchAllRecipes;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		mockParseFormula();
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
	 * Simulate usecase: get formula from API payload and evalute it true or not
	 * Input: formula, lineItemId
	 * Output: true or false
	 * Notes:
	 * - Pay attention to "==" operation
	 * - Can FE provide payload product.getType() instead of product.type ?
	 * - Main function: ParseFormula.parseFormula(formula, lineItemId)
	 */
	private static void mockParseFormula() {
		// this formula provided from API payload
		String formula = "(product.type == \"standalone\" && category.name == \"Hardware Product\")"
				+ "|| product.tag.bg == \"CNS\""
				+ "|| lineItem.quantity > 10"
				+ "|| lineItem.locationName == \"HYDERABAD\"";

		// Can FE provide payload like this ?
		// "(product.getType() == \"standalone\" && category.getName() == \"Hardware Product\") || product.getTag().getBg() == \"CNS\" || lineItem.getQuantity() > 10 || lineItem.getLocationName() == \"HYDERABAD\""
		System.out.println("Result: " + ParseFormula.parseFormula(formula, "lineitem1"));
	}

}
