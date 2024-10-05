package com.example.demo;

import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.PriceRecipeRange;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.service.PricingAdjustmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingAdjustmentServiceTest {
    private PricingAdjustmentService service;
    private ProfilingRequestDTO profilingRequestDTO;
    private PriceRecipe priceRecipe;

    @BeforeEach
    void setUp() {
        service = new PricingAdjustmentService();
        profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(new ArrayList<>());
        profilingRequestDTO.setDiscountDetails(new ArrayList<>());
        priceRecipe = new PriceRecipe();
        priceRecipe.setRanges(new ArrayList<>());
    }

    @Test
    void testCalculateCumulativeRange_WithDiscountPercentage() {
        // Setup test data
        List<LineItem> lineItems = Arrays.asList(
                new LineItem("1", "Product A", "Monthly", "Tag1", "Category1", "Family1", 100.0, "Config1", "TimeDim1", 10.0, "1"),
                new LineItem("2", "Product B", "Monthly", "Tag1", "Category1", "Family1", 200.0, "Config2", "TimeDim1", 20.0, "1")
        );

        // Add line items to profilingRequestDTO
        profilingRequestDTO.setLineItems(lineItems);

        // Create PriceRecipeRange with discount strategy
        PriceRecipeRange range = new PriceRecipeRange();
        range.setStartTier(10);
        range.setEndTier(50);
        range.setDealStrategy("discount");
        range.setApplicationType("%");
        range.setApplicationValue("10");
        range.setSourceDimensionName(List.of("TimeDim1"));
        priceRecipe.setRanges(List.of(range));

        // Call method under test
        service.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

        // Verify discount details
        assertEquals(2, profilingRequestDTO.getDiscountDetails().size());

        // Verify that net prices are adjusted
        assertEquals(lineItems.get(0).getNetPrice(), profilingRequestDTO.getDiscountDetails().get(0).getAppliedOnAmount());
        assertEquals(90.0, profilingRequestDTO.getDiscountDetails().get(0).getAfterAdjustment());
        assertEquals(lineItems.get(1).getNetPrice(), profilingRequestDTO.getDiscountDetails().get(1).getAppliedOnAmount());
        assertEquals(180.0, profilingRequestDTO.getDiscountDetails().get(1).getAfterAdjustment());
    }



    @Test
    void testCalculateCumulativeRange_WithMarkupAmount() {
        // Setup test data
        List<LineItem> lineItems = List.of(
                new LineItem("1", "Product C", "Monthly", "Tag2", "Category1", "Family1", 150.0, "Config1", "TimeDim2", 5.0, "1")
        );

        profilingRequestDTO.setLineItems(lineItems);

        // Create PriceRecipeRange with markup strategy
        PriceRecipeRange range = new PriceRecipeRange();
        range.setStartTier(1);
        range.setEndTier(10);
        range.setDealStrategy("markup");
        range.setApplicationType("Amount");
        range.setApplicationValue("20");
        range.setSourceDimensionName(List.of("TimeDim2"));
        priceRecipe.setRanges(List.of(range));

        // Call method under test
        service.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

        // Verify that net prices are adjusted
        assertEquals(lineItems.getFirst().getNetPrice(), profilingRequestDTO.getDiscountDetails().getFirst().getAppliedOnAmount());
        assertEquals(170.0, profilingRequestDTO.getDiscountDetails().getFirst().getAfterAdjustment());


        // Verify discount details
        assertEquals(1, profilingRequestDTO.getDiscountDetails().size());
    }

    @Test
    void testCalculateCumulativeRange_NoMatchingTimeDimension() {
        // Setup test data
        List<LineItem> lineItems = List.of(
                new LineItem("1", "Product D", "Monthly", "Tag3", "Category1", "Family1", 250.0, "Config1", "TimeDim3", 15.0, "1")
        );

        profilingRequestDTO.setLineItems(lineItems);

        // Create PriceRecipeRange with different timeDimensionName
        PriceRecipeRange range = new PriceRecipeRange();
        range.setStartTier(1);
        range.setEndTier(10);
        range.setDealStrategy("discount");
        range.setApplicationType("%");
        range.setApplicationValue("10");
        range.setSourceDimensionName(List.of("TimeDim4"));
        priceRecipe.setRanges(List.of(range));

        // Call method under test
        service.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

        // Verify no discount details
        assertEquals(0, profilingRequestDTO.getDiscountDetails().size());
    }

    @Test
    void testCalculateCumulativeRange_QuantityOutOfRange() {
        // Setup test data
        List<LineItem> lineItems = List.of(
                new LineItem("1", "Product E", "Monthly", "Tag4", "Category1", "Family1", 300.0, "Config1", "TimeDim5", 2.0, "1")
        );

        profilingRequestDTO.setLineItems(lineItems);

        // Create PriceRecipeRange with startTier and endTier that do not match
        PriceRecipeRange range = new PriceRecipeRange();
        range.setStartTier(10);
        range.setEndTier(20);
        range.setDealStrategy("discount");
        range.setApplicationType("%");
        range.setApplicationValue("20");
        range.setSourceDimensionName(List.of("TimeDim5"));
        priceRecipe.setRanges(List.of(range));

        // Call method under test
        service.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

        // Verify no discount details
        assertEquals(0, profilingRequestDTO.getDiscountDetails().size());
    }

    @Test
    void testCalculateCumulativeRange_WithMultipleGroups() {
        // Setup test data with multiple groups
        List<LineItem> lineItems = Arrays.asList(
                new LineItem("1", "Product F", "Monthly", "Tag5", "Category1", "Family1", 120.0, "Config1", "TimeDim6", 10.0, "1"),
                new LineItem("2", "Product G", "Monthly", "Tag5", "Category1", "Family1", 220.0, "Config1", "TimeDim6", 20.0, "1"),
                new LineItem("3", "Product H", "Monthly", "Tag5", "Category1", "Family1", 180.0, "Config1", "TimeDim7", 15.0, "1")
        );

        profilingRequestDTO.setLineItems(lineItems);

        // Create PriceRecipeRanges
        PriceRecipeRange range1 = new PriceRecipeRange();
        range1.setStartTier(10);
        range1.setEndTier(50);
        range1.setDealStrategy("discount");
        range1.setApplicationType("%");
        range1.setApplicationValue("15");
        range1.setSourceDimensionName(List.of("TimeDim6"));

        PriceRecipeRange range2 = new PriceRecipeRange();
        range2.setStartTier(1);
        range2.setEndTier(20);
        range2.setDealStrategy("markup");
        range2.setApplicationType("Amount");
        range2.setApplicationValue("30");
        range2.setSourceDimensionName(List.of("TimeDim7"));

        priceRecipe.setRanges(Arrays.asList(range1, range2));

        // Call method under test
        service.calculateCumulativeRange(priceRecipe, profilingRequestDTO);

        // Discount applied
        assertEquals(lineItems.getFirst().getNetPrice(), profilingRequestDTO.getDiscountDetails().getFirst().getAppliedOnAmount());
        assertEquals(102.0, profilingRequestDTO.getDiscountDetails().get(0).getAfterAdjustment());
        assertEquals(lineItems.getFirst().getNetPrice(), profilingRequestDTO.getDiscountDetails().getFirst().getAppliedOnAmount());
        assertEquals(187.0, profilingRequestDTO.getDiscountDetails().get(1).getAfterAdjustment());
        // Markup applied
        assertEquals(lineItems.getFirst().getNetPrice(), profilingRequestDTO.getDiscountDetails().getFirst().getAppliedOnAmount());
        assertEquals(210.0, profilingRequestDTO.getDiscountDetails().get(2).getAfterAdjustment());



        // Verify discount details
        assertEquals(3, profilingRequestDTO.getDiscountDetails().size());
    }
}
