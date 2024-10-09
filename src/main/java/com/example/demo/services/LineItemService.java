package com.example.demo.services;

import com.example.demo.models.Category;
import com.example.demo.models.LineItem;
import com.example.demo.models.Product;
import com.example.demo.models.Tag;

import java.util.List;

// MODIFY this code in real use case
public class LineItemService {
    // TRUE case:
    public static Tag tagTrue = new Tag(){
        {
            setBg("CNS");
            setValue(List.of("CNS", "ABC"));
        }
    };

    public static Product prodTrue = new Product(){
        {
            setType("standalon"); // typo on purpose
            setTag(tagTrue);
        }
    };

    public static Category categoryTrue = new Category(){
        {
            setName("Hardware Product");
        }
    };

    public static LineItem lineItemTrue = new LineItem(){
        {
            setQuantity(12d);
            setLocationName("HYDERABAD");
        }
    };

    // Mock callable methods - don't use static in real service
    public static Product getProductById(String id) {
        // fetch the product from the database
        return prodTrue;
    }

    public static Category getCategoryById(String id) {
        // fetch the category from the database
        return categoryTrue;
    }

    public static LineItem getLineItemById(String id) {
        // fetch the tag from the database
        return lineItemTrue;
    }
}
