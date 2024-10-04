package com.example.demo;

import com.example.demo.models.PriceList;
import com.example.demo.models.PriceListItem;
import com.example.demo.models.PriceProfileStep;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.demo.utils.MockDataGenerator.fetchAllRecipes;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

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
}
