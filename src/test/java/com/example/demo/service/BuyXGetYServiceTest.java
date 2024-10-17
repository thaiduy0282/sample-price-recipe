package com.example.demo.service;

import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.models.buyXGetY.Adjustment;
import com.example.demo.models.buyXGetY.BuyConditionGroup;
import com.example.demo.models.buyXGetY.BuySection;
import com.example.demo.models.buyXGetY.Condition;
import com.example.demo.models.buyXGetY.GetSection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class BuyXGetYServiceTest {
    private final BuyXGetYService service = new BuyXGetYService();

    @Test
    public void testBestAdjustment() {
        // Create sample line items (cart)
        LineItem iphone = createLineItem("1", "iPhone", 4, 100.0);
        LineItem ipad = createLineItem("2", "iPad", 2, 70.0);
        LineItem samsung = createLineItem("3", "Samsung", 6, 50.0);

        List<LineItem> cart = Arrays.asList(iphone, ipad, samsung);
        ProfilingRequestDTO requestDTO = ProfilingRequestDTO.builder().lineItems(cart).build();

        // Create BuyConditionGroups for the recipe
        BuyConditionGroup group1 = new BuyConditionGroup(
            new BuySection(List.of(new Condition("iPhone", 2),
                new Condition("iPad", 1))),
            new GetSection(List.of(new Adjustment("Samsung", "Discount", "Percentage", 4, 100.0)))
        );

        BuyConditionGroup group2 = new BuyConditionGroup(
            new BuySection(List.of(new Condition("Samsung", 6))),
            new GetSection(List.of(new Adjustment("Samsung", "Discount", "Percentage", 3, 100.0)))
        );

        // Create the recipe
        PriceRecipe recipe = PriceRecipe.builder()
            .dealStrategy("discount")
            .applicationType("Percentage")
            .conditionGroups(List.of(group1, group2)).build();

        // Calculate the optimal price
        BuyXGetYService service = new BuyXGetYService();
        service.calculatePriceOneOff(recipe, requestDTO);

        // Print the updated cart
        requestDTO.getLineItems().forEach(System.out::println);
    }

    private LineItem createLineItem(String id, String productId, int quantity, double netPrice) {
        return LineItem.builder()
            .id(id)
            .productId(productId)
            .quantity((double) quantity)
            .netPrice(netPrice)
            .build();
    }
}
