package com.example.demo;

import com.example.demo.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

	private DemoApplication application;

	@BeforeEach
	public void setup() {
		application = new DemoApplication();
	}

	@Test
	public void testCalculateCumulativeRange_DiscountApplied() {
		// Arrange: Create dummy LineItems
		List<LineItem> lineItems = Arrays.asList(
				new LineItem("Model1", "Monthly", "Tag1", "Category1", "Family1", 100.0, "Config1", "TimeDim1", 50.0),
				new LineItem("Model2", "Quarterly", "Tag2", "Category2", "Family2", 200.0, "Config2", "TimeDim1", 30.0),
				new LineItem("Model3", "Yearly", "Tag3", "Category3", "Family3", 300.0, "Config3", "TimeDim2", 150.0)
		);

		// Arrange: Create dummy PriceRecipeRange with discount
		PriceRecipeRange range1 = new PriceRecipeRange(40.0, 100.0, "discount", "%", "10", List.of("TimeDim1"), List.of("TimeDim1"));
		PriceRecipeRange range2 = new PriceRecipeRange(100.0, 200.0, "discount", "Amount", "50", List.of("TimeDim2"), List.of("TimeDim2"));

		List<PriceRecipeRange> priceRecipeRanges = Arrays.asList(range1, range2);

		// Arrange: Create PriceRecipe
		PriceRecipe priceRecipe = new PriceRecipe();
		priceRecipe.setRanges(priceRecipeRanges);

		// Arrange: Create ProfilingRequestDTO
		ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
		profilingRequestDTO.setLineItems(lineItems);

		// Act: Call the method
		application.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

		// Assert: Verify the results
		// For TimeDim1, expect a 10% discount applied to both line items
		assertEquals(90.0, lineItems.get(0).getNetPrice()); // 10% discount applied on 100
		assertEquals(180.0, lineItems.get(1).getNetPrice()); // 10% discount applied on 200

		// Expect discount of 50 applied to the item in TimeDim2
		assertEquals(250.0, lineItems.get(2).getNetPrice()); // Discount applied: 300 - 50

	}

	@Test
	public void testCalculateCumulativeRange_MarkupApplied() {
		// Arrange: Create dummy LineItems with larger quantities for testing markup
		List<LineItem> lineItems = List.of(
                new LineItem("Model1", "Monthly", "Tag1", "Category1", "Family1", 100.0, "Config1", "TimeDim2", 150.0)
        );

		// Arrange: Create PriceRecipeRange with markup
		PriceRecipeRange range1 = new PriceRecipeRange(100.0, 200.0, "markup", "Amount", "50", List.of("TimeDim2"), List.of("TimeDim2"));

		List<PriceRecipeRange> priceRecipeRanges = List.of(range1);

		// Arrange: Create PriceRecipe
		PriceRecipe priceRecipe = new PriceRecipe();
		priceRecipe.setRanges(priceRecipeRanges);

		// Arrange: Create ProfilingRequestDTO
		ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
		profilingRequestDTO.setLineItems(lineItems);

		// Act: Call the method
		application.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

		// Assert: Verify the result
		// Expect markup of 50 applied to the item in TimeDim2
		assertEquals(150.0, lineItems.getFirst().getNetPrice()); // Markup applied: 100 + 50
	}

	@Test
	public void testCalculateCumulativeRange_NoChange() {
		// Arrange: Create dummy LineItems
		List<LineItem> lineItems = List.of(
                new LineItem("Model1", "Monthly", "Tag1", "Category1", "Family1", 100.0, "Config1", "TimeDim3", 5.0)
        );

		// Arrange: Create dummy PriceRecipeRange that does not apply to any LineItem
		PriceRecipeRange range1 = new PriceRecipeRange(40.0, 100.0, "discount", "%", "10", Arrays.asList("TimeDim1"), Arrays.asList("TimeDim1"));

		List<PriceRecipeRange> priceRecipeRanges = List.of(range1);

		// Arrange: Create PriceRecipe
		PriceRecipe priceRecipe = new PriceRecipe();
		priceRecipe.setRanges(priceRecipeRanges);

		// Arrange: Create ProfilingRequestDTO
		ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
		profilingRequestDTO.setLineItems(lineItems);

		// Act: Call the method
		application.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

		// Assert: Verify that no changes occurred
		assertEquals(100.0, lineItems.getFirst().getNetPrice()); // No changes expected
	}

}
