package com.example.demo.service;

import com.example.demo.models.Category;
import com.example.demo.models.LineItem;
import com.example.demo.models.Product;
import com.example.demo.models.Tag;

// MODIFY this code in real use case
public class LineItemService {
    // TRUE case:
    public static Tag tagTrue = new Tag(){
        {
            setBg("CNS");
        }
    };

    public static Product prodTrue = new Product(){
        {
            setType("standalone");
            setTag(tagTrue);
        }
    };

    public static Category categoryTrue = new Category(){
        {
            setName("Hardware Product");
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
//        return new LineItem(){
//            {
//                setQuantity(12);
//                setLocationName("HYDERABAD");
//            }
//        };
        return null; //todo: temp
    }
}
