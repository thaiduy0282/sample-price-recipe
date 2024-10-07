package com.example.demo.service;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.utils.FormulaEvaluator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class SimplePricingOneOffServiceTest {

    private SimplePricingOneOffService pricingStrategy;

    private MockedStatic<FormulaEvaluator> mockedStatic;

    @BeforeEach
    public void setUp() {
        pricingStrategy = new SimplePricingOneOffService();
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
        List<LineItem> lineItems = List.of(
                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2024", 50.0, "ProductA"),
                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2025", 10.0, "ProductA"),
                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2025", 1.0, "ProductB")
        );

        // Set up DisCountDetails for lineItem lineItem-123
        DiscountDetails discountDetail = new DiscountDetails(
                "%",        // adjustmentType
                10.0,               // adjustmentValue
                100.0,              // appliedOnAmount
                90.0,               // afterAdjustment
                100.0,               // netPrice
                System.currentTimeMillis(), // discountDate
                "Promo Code",       // discountSource
                "DISCOUNT10",       // discountCode
                "ProductB",       // productConfigurationId
                "lineItem-123",     // lineItemId
                1,                  // sequence
                "ref-789",          // referenceId
                "recipe-012",       // recipeId
                "all"               // appliedTo
        );
        discountDetail.setName("netPrice");
        ArrayList<DiscountDetails> discountDetails = new ArrayList<>();
        discountDetails.add(discountDetail);

        // Mock the ProfilingRequestDTO
        ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(lineItems);
        profilingRequestDTO.setDiscountDetails(discountDetails);

        // Run calculatePrice method
        pricingStrategy.calculatePrice(priceRecipe, profilingRequestDTO);


        // Verify that the discount details were added
        List<DiscountDetails> discountDetailsAfter = profilingRequestDTO.getDiscountDetails();
        assertEquals(3, discountDetailsAfter.size());

        List<DiscountDetails> lineItem123DiscountDetailsList = discountDetailsAfter.stream()
                .filter(discount -> discount.getLineItemId().equals("lineItem-123"))
                .sorted(Comparator.comparingInt(DiscountDetails::getSequence))
                .toList();
        assertFalse(lineItem123DiscountDetailsList.isEmpty());

        double initialPrice = 100d;
        for (DiscountDetails discount : lineItem123DiscountDetailsList) {
            initialPrice = initialPrice - (initialPrice * (discount.getAdjustmentValue() / 100));
        }

        assertEquals(lineItem123DiscountDetailsList.getLast().getAfterAdjustment(), initialPrice);
    }

    @Test
    void testCalculatePriceFixedDiscount() {

        double initialPrice = 100d;
        double adjustmentValue = 15.0;

        // Set up mock PriceRecipe
        PriceRecipe priceRecipe = new PriceRecipe();
        priceRecipe.setApplicationType("amount");
        priceRecipe.setApplicationValue(adjustmentValue); // Fixed discount of 15.0
        priceRecipe.setPriceApplicationON("netPrice");
        priceRecipe.setPriceAppliedTo("referencePrice");

        // Set up LineItem
        List<LineItem> lineItems = List.of(
                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2024", 50.0, "ProductA"),
                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2025", 10.0, "ProductA"),
                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
                        100.0, 100.0, 100.0, 100.0, 100.0, "Configuration1", "2025", 1.0, "ProductB")
        );

        // Set up DisCountDetails for lineItem lineItem-123
        DiscountDetails discountDetail = new DiscountDetails(
                "amount",        // adjustmentType
                10.0,               // adjustmentValue
                100.0,              // appliedOnAmount
                85.0,               // afterAdjustment
                100.0,               // netPrice
                System.currentTimeMillis(), // discountDate
                "Promo Code",       // discountSource
                "DISCOUNT10",       // discountCode
                "ProductB",       // productConfigurationId
                "lineItem-123",     // lineItemId
                1,                  // sequence
                "ref-789",          // referenceId
                "recipe-012",       // recipeId
                "all"               // appliedTo
        );
        discountDetail.setName("referencePrice");
        ArrayList<DiscountDetails> discountDetails = new ArrayList<>();
        discountDetails.add(discountDetail);

        // Mock the ProfilingRequestDTO
        ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(lineItems);
        profilingRequestDTO.setDiscountDetails(discountDetails);

        // Run calculatePrice method
        pricingStrategy.calculatePrice(priceRecipe, profilingRequestDTO);

        // Verify that the discount details were added
        List<DiscountDetails> discountDetailsAfter = profilingRequestDTO.getDiscountDetails();
        assertEquals(3, discountDetailsAfter.size());

        List<DiscountDetails> lineItem123DiscountDetailsList = discountDetailsAfter.stream()
                .filter(discount -> discount.getLineItemId().equals("lineItem-123"))
                .sorted(Comparator.comparingInt(DiscountDetails::getSequence))
                .toList();

        assertFalse(lineItem123DiscountDetailsList.isEmpty());

        double lastAfterAdjustment = initialPrice - lineItem123DiscountDetailsList.size() * adjustmentValue;

        assertEquals(lineItem123DiscountDetailsList.getLast().getAfterAdjustment(), lastAfterAdjustment);

    }
}
