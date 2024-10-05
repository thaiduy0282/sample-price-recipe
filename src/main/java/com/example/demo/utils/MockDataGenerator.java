package com.example.demo.utils;

import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;

import java.util.ArrayList;
import java.util.List;

public class MockDataGenerator {
    public static List<PriceRecipe> fetchAllRecipes() {
        List<PriceRecipe> recipes = new ArrayList<>();
        recipes.add(new PriceRecipe("1", "contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("2", "contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "dealMax", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("3", "contract", "ae7ae002-dd1c-4e00-895f-6e0646f15196", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));
        recipes.add(new PriceRecipe("4", "quote", "ae7ae002-dd1c-4e00-895f-111111111111", "range", "Type1", "DealStrategy1", "ApplicationON1", "Execution1", "Condition1", "ApplicationType1", 100.0, "AppliedOn1", "PriceAppliedTo1", "Dimension1", new ArrayList<>()));

        return recipes;
    }

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
        lineItems.add(new LineItem("10","Model B", "Monthly", "Tag2", "Category2", "Family2", 22.50, "Config10", "2024",100d, "1"));

        return lineItems;
    }
}
