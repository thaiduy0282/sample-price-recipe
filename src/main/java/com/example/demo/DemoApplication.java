package com.example.demo;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.PriceList;
import com.example.demo.models.PriceListItem;
import com.example.demo.models.PriceProfileStep;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.service.CumulativeRangeService;
import com.example.demo.service.ISimplePricingOneOffService;
import com.example.demo.service.impl.SimplePricingOneOffService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.demo.utils.MockDataGenerator.*;

@SpringBootApplication
public class DemoApplication {

	private static final CumulativeRangeService CUMULATIVE_RANGE_SERVICE = new CumulativeRangeService();

	private static final ISimplePricingOneOffService simplePricingOnOffService = new SimplePricingOneOffService();


	public static void main(String[] args) {}


	public static List<DiscountDetails> executeVolume(List<PriceProfileStep> steps, ProfilingRequestDTO profilingRequestDTO, Map<String, List<PriceListItem>> priceListItemMap, Map<String, List<PriceList>> priceListById) {
		// Fetching all the recipes
		List<PriceRecipe> recipes = fetchAllRecipes();

		// Sorting the priceProfileStep base on the sequence field
		steps.sort(Comparator.comparingInt(PriceProfileStep::getSequence));

		// Loop through each step in the priceProfileStep list and apply the discount based on the recipe settings and price point.
		for (PriceProfileStep step : steps) {
			// Filtering the recipe from the recipes list based scope/scopeValue/priceSettings/PriceApplicationOn
			List<PriceRecipe> matchingRecipes = recipes.stream().filter(r -> Objects.equals(r.getScope(), step.getScope())
									&& Objects.equals(r.getScopeValue(), step.getScopeValue())
									&& Objects.equals(r.getPriceSetting(), step.getPriceSetting())
									&& Objects.equals(r.getPriceApplicationON(), step.getPricePoint())).toList();

			// Loop into each of the matching recipes and process them
			for (PriceRecipe recipe : matchingRecipes) {
				switch(recipe.getPriceSetting()) {
					case "simplePricing":
						if ("OneOff".equals(recipe.getType())) {
							simplePricingOnOffService.calculatePrice(recipe, profilingRequestDTO);
						}
						break;
					case "dealMax":
						// code block
						break;
					case "buyXGetY":
						// code block
						break;
					case "cumulativeRange":
						CUMULATIVE_RANGE_SERVICE.calculateCumulativeRange(recipe, profilingRequestDTO);
						break;
					default:
						// not found type
				}
			}
		}

		// return this object for testing purpose only.
		return profilingRequestDTO.getDiscountDetails();
	}
}
