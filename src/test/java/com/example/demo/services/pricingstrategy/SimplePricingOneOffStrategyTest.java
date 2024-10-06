package com.example.demo.services.pricingstrategy;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.services.pricingstrategy.impl.SimplePricingOneOffStrategy;
import com.example.demo.utils.FormulaEvaluator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class SimplePricingOneOffStrategyTest {

    private SimplePricingOneOffStrategy pricingStrategy;
    private MockedStatic<FormulaEvaluator> mockedStatic;

    @BeforeEach
    public void setUp() {
        pricingStrategy = new SimplePricingOneOffStrategy();
        mockedStatic = Mockito.mockStatic(FormulaEvaluator.class);
        mockedStatic.when(() -> FormulaEvaluator.evaluateFormula(any(), any())).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    void testCalculatePricePercentageDiscount() {
        // Set up mock PriceRecipe
        PriceRecipe priceRecipe = new PriceRecipe();
        priceRecipe.setApplicationType("%");
        priceRecipe.setApplicationValue(10.0); // 10% discount
        priceRecipe.setPriceApplicationON("netPrice"); // Field to apply discount
        priceRecipe.setPriceAppliedTo("netPrice");

        // Set up LineItem
        LineItem lineItem = new LineItem();
        lineItem.setNetPrice(100.0); // Original price

        List<LineItem> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        // Mock the ProfilingRequestDTO
        ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(lineItems);
        profilingRequestDTO.setDiscountDetails(new ArrayList<>());

        // Run calculatePrice method
        pricingStrategy.calculatePrice(priceRecipe, profilingRequestDTO);

        // Verify that the price was updated correctly
        assertEquals(90.0, lineItem.getNetPrice()); // 10% discount applied to 100.0

        // Verify that the discount details were added
        List<DiscountDetails> discountDetails = profilingRequestDTO.getDiscountDetails();
        assertEquals(1, discountDetails.size());

        DiscountDetails discountDetail = discountDetails.get(0);
        assertEquals(10.0, discountDetail.getAdjustmentValue());
        assertEquals("netPrice", discountDetail.getAppliedTo());
        assertEquals(100.0, discountDetail.getAppliedOnAmount());
        assertEquals(90.0, discountDetail.getAfterAdjustment());
    }

    @Test
    void testCalculatePriceFixedDiscount() {
        // Set up mock PriceRecipe
        PriceRecipe priceRecipe = new PriceRecipe();
        priceRecipe.setApplicationType("amount");
        priceRecipe.setApplicationValue(15.0); // Fixed discount of 15.0
        priceRecipe.setPriceApplicationON("netPrice");
        priceRecipe.setPriceAppliedTo("netPrice");

        // Set up LineItem
        LineItem lineItem = new LineItem();
        lineItem.setNetPrice(100.0); // Original price

        List<LineItem> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        // Mock the ProfilingRequestDTO
        ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(lineItems);
        profilingRequestDTO.setDiscountDetails(new ArrayList<>());

        // Run calculatePrice method
        pricingStrategy.calculatePrice(priceRecipe, profilingRequestDTO);

        // Verify that the price was updated correctly
//        assertEquals(85.0, lineItem.getNetPrice()); // 15 fixed discount applied to 100.0

        // Verify that the discount details were added
        List<DiscountDetails> discountDetails = profilingRequestDTO.getDiscountDetails();
        assertEquals(1, discountDetails.size());

        DiscountDetails discountDetail = discountDetails.get(0);
        assertEquals(15.0, discountDetail.getAdjustmentValue());
        assertEquals("netPrice", discountDetail.getAppliedTo());
        assertEquals(100.0, discountDetail.getAppliedOnAmount());
        assertEquals(85.0, discountDetail.getAfterAdjustment());
    }
}
