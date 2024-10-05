package com.example.demo.utils;

import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;

import java.util.ArrayList;
import java.util.List;

public class MockDataGenerator {
    public static List<PriceRecipe> fetchAllRecipes() {
        List<PriceRecipe> recipes = new ArrayList<>();
        recipes.add(new PriceRecipe("contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "dealMax", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("quote", "ae7ae002-dd1c-4e00-895f-111111111111", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));

        return recipes;
    }

    public static List<LineItem> fetchLineItems() {
        ArrayList<LineItem> lineItems = new ArrayList<>();

        lineItems.add(new LineItem("Model A", "Daily", "Tag1", "Category1", "Family1", 45.67, "Config1", "Location1", 1));
        lineItems.add(new LineItem("Model B", "Weekly", "Tag2", "Category2", "Family2", 78.23, "Config2", "Location2", 2));
        lineItems.add(new LineItem("Model C", "Monthly", "Tag3", "Category3", "Family3", 32.89, "Config3", "Location3", 3));
        lineItems.add(new LineItem("Model D", "Yearly", "Tag4", "Category4", "Family4", 15.50, "Config4", "Location4", 4));
        lineItems.add(new LineItem("Model A", "Monthly", "Tag1", "Category1", "Family1", 99.99, "Config5", "Location5", 5));
        lineItems.add(new LineItem("Model B", "Monthly", "Tag2", "Category2", "Family2", 29.99, "Config6", "Location6", 6));
        lineItems.add(new LineItem("Model C", "Weekly", "Tag3", "Category3", "Family3", 55.45, "Config7", "Location7", 7));
        lineItems.add(new LineItem("Model D", "Yearly", "Tag4", "Category4", "Family4", 40.00, "Config8", "Location8", 8));
        lineItems.add(new LineItem("Model A", "Daily", "Tag1", "Category1", "Family1", 66.88, "Config9", "Location9", 9));
        lineItems.add(new LineItem("Model B", "Monthly", "Tag2", "Category2", "Family2", 22.50, "Config10", "Location10", 10));

        return lineItems;
    }
}
