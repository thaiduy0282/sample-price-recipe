//package com.example.demo;
//
//import com.example.demo.models.*;
//import com.example.demo.service.PricingAdjustmentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PricingAdjustmentServiceTest {
//    private PricingAdjustmentService pricingAdjustmentService;
//    private PriceRecipe priceRecipe;
//    private ProfilingRequestDTO profilingRequest;
//
//    @BeforeEach
//    public void setUp() {
//        pricingAdjustmentService = new PricingAdjustmentService();
//        priceRecipe = new PriceRecipe();
//        profilingRequest = new ProfilingRequestDTO();
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_WithDiscountAmount() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 10.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Discount", "Amount", "10",
//                List.of("2024"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(2, profilingRequest.getDiscountDetails().size());
//        assertEquals("ProductA", profilingRequest.getDiscountDetails().getLast().getProductConfigurationId());
//        assertEquals(priceApplicationON - priceApplicationON*Double.parseDouble(range.getApplicationValue())/100, profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_WithDiscountPercentage() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 10.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Discount", "%", "10",
//                List.of("2024"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(2, profilingRequest.getDiscountDetails().size());
//        assertEquals("ProductA", profilingRequest.getDiscountDetails().getLast().getProductConfigurationId());
//        assertEquals(priceApplicationON - Double.parseDouble(range.getApplicationValue()), profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_WithMarkupAmount() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 10.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Markup", "Amount", "10",
//                List.of("2024"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(2, profilingRequest.getDiscountDetails().size());
//        assertEquals("ProductA", profilingRequest.getDiscountDetails().getLast().getProductConfigurationId());
//        assertEquals(priceApplicationON + Double.parseDouble(range.getApplicationValue()), profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_WithMarkupPercentage() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 10.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Markup", "%", "10",
//                List.of("2024"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(2, profilingRequest.getDiscountDetails().size());
//        assertEquals("ProductA", profilingRequest.getDiscountDetails().getLast().getProductConfigurationId());
//        assertEquals(priceApplicationON + priceApplicationON*Double.parseDouble(range.getApplicationValue())/100, profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//
//    @Test
//    public void testCalculateCumulativeRange_NoMatchingTimeDimension() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 10.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Markup", "%", "10",
//                List.of("2022-01"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(1, profilingRequest.getDiscountDetails().size());
//        assertEquals(priceApplicationON, profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_QuantityOutOfRange() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 150.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 100.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        List<PriceRecipeRange> ranges = new ArrayList<>();
//        PriceRecipeRange range = new PriceRecipeRange(50.0, 100.0, "Markup", "%", "10",
//                List.of("2024"), List.of("2025"));
//        ranges.add(range);
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(1, profilingRequest.getDiscountDetails().size());
//        assertEquals(priceApplicationON, profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//    @Test
//    public void testCalculateCumulativeRange_WithMultipleRanges() {
//
//        List<LineItem> lineItems = List.of(
//                new LineItem("lineItem-123", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2024", 50.0, "ProductA"),
//                new LineItem("lineItem-345", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 100.0, "ProductA"),
//                new LineItem("lineItem-456", "ModelA", "Weekly", "Tag1", "Category1", "Family1",
//                        100.0, "Configuration1", "2025", 1.0, "ProductB")
//        );
//
//        profilingRequest.setLineItems(lineItems);
//
//        PriceRecipeRange range1 = new PriceRecipeRange(50.0, 100.0, "Markup", "%", "10",
//                List.of("2024"), List.of("2025"));
//
//        PriceRecipeRange range2 = new PriceRecipeRange(50.0, 100.0, "Markup", "%", "10",
//                List.of("2024"), List.of("2025"));
//
//        List<PriceRecipeRange> ranges = List.of(range1, range2);
//
//        priceRecipe.setRanges(ranges);
//        double priceApplicationON = 100;
//        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
//
//        DiscountDetails discount = new DiscountDetails(
//                "%",        // adjustmentType
//                0.0,               // adjustmentValue
//                priceApplicationON,              // appliedOnAmount
//                priceApplicationON,               // afterAdjustment
//                priceApplicationON,               // netPrice
//                System.currentTimeMillis(), // discountDate
//                "Promo Code",       // discountSource
//                "DISCOUNT10",       // discountCode
//                "ProductB",       // productConfigurationId
//                "lineItem-345",     // lineItemId
//                1,                  // sequence
//                "ref-789",          // referenceId
//                "recipe-012",       // recipeId
//                "all"               // appliedTo
//        );
//        discount.setName(String.valueOf(priceApplicationON));
//
//
//        profilingRequest.setDiscountDetails(List.of(discount));
//
//
//        pricingAdjustmentService.calculateCumulativeRange(priceRecipe, profilingRequest);
//
//
//        assertEquals(3, profilingRequest.getDiscountDetails().size());
//        double afterAdjustment1 = priceApplicationON + priceApplicationON*Double.parseDouble(range1.getApplicationValue())/100;
//        double afterAdjustment2 = afterAdjustment1 + afterAdjustment1*Double.parseDouble(range1.getApplicationValue())/100;
//        assertEquals(afterAdjustment2, profilingRequest.getDiscountDetails().getLast().getAfterAdjustment());
//    }
//
//}
