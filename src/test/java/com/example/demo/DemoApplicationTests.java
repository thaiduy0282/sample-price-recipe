package com.example.demo;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceList;
import com.example.demo.models.PriceListItem;
import com.example.demo.models.PriceProfileStep;
import com.example.demo.models.ProfilingRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {


	@Test
	void verifyCumulativeRangeWorkAsExpected() {
		List<PriceProfileStep> steps = initProfileSteps();
		ProfilingRequestDTO profilingRequestDTO = initProfileRequestDTO();
		Map<String, List< PriceListItem >> priceListItemMap = new HashMap<>();
		Map<String, List< PriceList >> priceListById = new HashMap<>();
		List<DiscountDetails> discountDetails = DemoApplication.executeVolume(steps, profilingRequestDTO, priceListItemMap, priceListById);

		for (DiscountDetails discountDetail : discountDetails) {
			System.out.println(discountDetail);
		}
	}

	private ProfilingRequestDTO initProfileRequestDTO() {
		ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
		profilingRequestDTO.setProfileName("TestProfile");
		profilingRequestDTO.setLineItems(initLineItems());
		profilingRequestDTO.setDiscountDetails(initDiscountDetails());
        return profilingRequestDTO;
	}

	private List<DiscountDetails> initDiscountDetails() {
		DiscountDetails discountDetails1 = new DiscountDetails("Percentage", 0.0, 1000.0, 1000.0, 0.0, System.currentTimeMillis(), "Source1", "DiscountCode1", "Config1", "lineItem-1", 1, "Ref1", "Recipe1", "All");
		discountDetails1.setName("PriceList");

		DiscountDetails discountDetails2 = new DiscountDetails("Percentage", 0.0, 1000.0, 1000.0, 0.0, System.currentTimeMillis(), "Source1", "DiscountCode1", "Config1", "lineItem-2", 2, "Ref1", "Recipe1", "All");
		discountDetails2.setName("PriceList");

		DiscountDetails discountDetails3 = new DiscountDetails("Percentage", 0.0, 1000.0, 1000.0, 0.0, System.currentTimeMillis(), "Source1", "DiscountCode1", "Config1", "lineItem-3", 3, "Ref1", "Recipe1", "All");
		discountDetails3.setName("PriceList");

		return List.of(
			discountDetails1,
			discountDetails2,
			discountDetails3
		);
	}

	private List<LineItem> initLineItems() {
		List<LineItem> lineItems = List.of(
				new LineItem("lineItem-1", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
						100.0, "Configuration1", "2024", 50.0, "ProductA"),
				new LineItem("lineItem-2", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
						100.0, "Configuration1", "2025", 10.0, "ProductA"),
				new LineItem("lineItem-3", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
						100.0, "Configuration1", "2025", 1.0, "ProductB")
		);

		return lineItems;
	}

	private List<PriceProfileStep> initProfileSteps() {
		List<PriceProfileStep> profileSteps = new ArrayList<>();
		profileSteps.add(new PriceProfileStep("ListPrice", 1, "recipe", "Quote", "quote1", "cumulativeRange"));
		profileSteps.add(new PriceProfileStep("ReferencePrice", 2, "recipe", "Quote", "quote1", "cumulativeRange"));
		profileSteps.add(new PriceProfileStep("ContractPrice", 3, "recipe", "Quote", "quote1", "cumulativeRange"));
		profileSteps.add(new PriceProfileStep("NetPrice", 4, "recipe", "Quote", "quote1", "cumulativeRange"));
		profileSteps.add(new PriceProfileStep("UnitPrice", 5, "recipe", "Quote", "quote1", "cumulativeRange"));

		return profileSteps;
	}

}
