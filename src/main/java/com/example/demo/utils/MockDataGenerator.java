package com.example.demo.utils;

import com.example.demo.models.*;

import java.util.*;

public class MockDataGenerator {

    public static List<LineItem> fetchLineItems() {
        ArrayList<LineItem> lineItems = new ArrayList<>();

        lineItems.add(new LineItem("1", "Model A", "Daily", "Tag1", "Category1", "Family1", 45.67, "Config1", "2024",100d, "1"));
        lineItems.add(new LineItem("2","Model B", "Weekly", "Tag2", "Category2", "Family2", 78.23, "Config2", "2024",100d, "1"));
        lineItems.add(new LineItem("3","Model C", "Monthly", "Tag3", "Category3", "Family3", 32.89, "Config3", "2024",100d, "1"));
        lineItems.add(new LineItem("4","Model D", "Yearly", "Tag4", "Category4", "Family4", 15.50, "Config4", "2024",100d, "1"));
        lineItems.add(new LineItem("5","Model A", "Monthly", "Tag1", "Category1", "Family1", 99.99, "Config5", "2024",100d, "1"));
        lineItems.add(new LineItem("6","Model B", "Monthly", "Tag2", "Category2", "Family2", 29.99, "Config6", "2024",100d, "1"));
        lineItems.add(new LineItem("7","Model C", "Weekly", "Tag3", "Category3", "Family3", 55.45, "Config7", "2024",100d, "1"));
        lineItems.add(new LineItem("8","Model D", "Yearly", "Tag4", "Category4", "Family4", 40.00, "Config8", "2024",100d, "1"));
        lineItems.add(new LineItem("9","Model A", "Daily", "Tag1", "Category1", "Family1", 66.88, "Config9", "2024",100d, "1"));
        lineItems.add(new LineItem("LineItem1","Model B", "Monthly", "Tag2", "Category2", "Family2", 22.50, "Config10", "2025",100d, "1"));

        return lineItems;
    }


    public static List<PriceProfileStep> createDummyPriceProfileSteps() {
        List<PriceProfileStep> steps = new ArrayList<>();
        PriceProfileStep step1 = new PriceProfileStep();
        step1.setPricePoint("1000");
        step1.setSequence(1);
        step1.setPricingMethod("RECIPE");
        step1.setScope("Product");
        step1.setScopeValue("Product123");
        step1.setPriceSetting("CumulativeRange");

        PriceProfileStep step2 = new PriceProfileStep();
        step2.setPricePoint("2000");
        step2.setSequence(2);
        step2.setPricingMethod("FORMULA");
        step2.setScope("Product");
        step2.setScopeValue("Product123");
        step2.setPriceSetting("DealMax");

        steps.add(step1);
        steps.add(step2);
        return steps;
    }

    public static ProfilingRequestDTO createDummyProfilingRequestDTO() {
        DiscountDetails discountDetails = new DiscountDetails("%", 10.0, 1000.0, 900.0, 900.0, System.currentTimeMillis(), "Source1", "DiscountCode1", "Config1", "LineItem1", 1, "Ref1", "Recipe1", "All");
        discountDetails.setName("1000");
        ProfilingRequestDTO profilingRequestDTO = new ProfilingRequestDTO();
        profilingRequestDTO.setLineItems(fetchLineItems());
        profilingRequestDTO.setDiscountDetails(Arrays.asList(discountDetails));
        profilingRequestDTO.setProfileName("TestProfile");
        profilingRequestDTO.setRepricing(true);
        profilingRequestDTO.setProductConfigurationId("Config1");

        return profilingRequestDTO;
    }

    public static Map<String, List<PriceListItem>> createDummyPriceListItemMap() {
        Map<String, List<PriceListItem>> priceListItemMap = new HashMap<>();
        PriceListItem priceListItem = new PriceListItem();
        priceListItemMap.put("Product123", Arrays.asList(priceListItem));
        return priceListItemMap;
    }

    public static Map<String, List<PriceList>> createDummyPriceListByIdMap() {
        Map<String, List<PriceList>> priceListById = new HashMap<>();
        PriceList priceList = new PriceList();
        priceListById.put("PriceList1", Arrays.asList(priceList));
        return priceListById;
    }

    public static List<PriceRecipe> fetchAllRecipes() {
        List<PriceRecipe> recipes = new ArrayList<>();

        PriceRecipe recipe1 = new PriceRecipe();
        recipe1.setScope("Product");
        recipe1.setScopeValue("Product123");
        recipe1.setPriceSetting("CumulativeRange");
        recipe1.setType("BuyXGetY");
        recipe1.setPriceApplicationON("1000");

        PriceRecipeRange range = new PriceRecipeRange();
        range.setStartTier(500);
        range.setEndTier(1000);
        range.setDealStrategy("discount");
        range.setApplicationType("%");
        range.setApplicationValue("10");
        range.setSourceDimensionName(List.of("2024"));
        range.setTargetDimensionName(List.of("2025"));

        recipe1.setRanges(List.of(range));
        recipes.add(recipe1);

        return recipes;
    }
}
