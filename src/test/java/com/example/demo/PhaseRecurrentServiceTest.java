package com.example.demo;

import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;
import com.example.demo.service.PhaseRecurrentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PhaseRecurrentServiceTest {

    private PhaseRecurrentService phaseRecurrentService;
    private ProfilingRequestDTO profilingRequest;

    @BeforeEach
    public void setUp() {
        phaseRecurrentService = new PhaseRecurrentService();
        profilingRequest = new ProfilingRequestDTO();
    }

    @Test
    void testPhaseRecurrentServiceCalculate() {
        double priceApplicationON = 100;

        // Set up mock PriceRecipe
        PriceRecipe priceRecipe = new PriceRecipe();
        priceRecipe.setPriceSetting("range");
        priceRecipe.setType("phaseRecurrent");
        priceRecipe.setApplicationType("Percentage");
        priceRecipe.setApplicationValue(10.0);
        priceRecipe.setPriceApplicationON(String.valueOf(priceApplicationON));
        priceRecipe.setPriceAppliedTo("netPrice");
        priceRecipe.setDealStrategy("discount");

        LineItem lineItem2014 = LineItem.builder()
                .id("lineItem1")
                .productId("product1")
                .startDate(3L)
                .timeDimensionName("2014")
                .build();
        LineItem lineItem2016 = LineItem.builder()
                .id("lineItem2")
                .productId("product1")
                .startDate(2L)
                .timeDimensionName("2016")
                .build();
        LineItem lineItem2015 = LineItem.builder()
                .id("lineItem3")
                .productId("product1")
                .startDate(1L)
                .timeDimensionName("2015")
                .build();
        profilingRequest.setLineItems(new ArrayList<>(List.of(lineItem2014, lineItem2016, lineItem2015)));

        DiscountDetails discount = DiscountDetails.builder()
                .adjustmentType("%")
                .adjustmentValue(0.0)
                .appliedOnAmount(priceApplicationON)
                .afterAdjustment(priceApplicationON)
                .netPrice(priceApplicationON)
                .discountDate(System.currentTimeMillis())
                .discountSource("Promo Code")
                .discountCode("DISCOUNT10")
                .productConfigurationId("ProductB")
                .lineItemId("lineItem1")
                .sequence(1)
                .recipeId("ref-789")
                .recipeId("recipe-012")
                .appliedTo("all")
                .name(String.valueOf(priceApplicationON))
                .build();
        profilingRequest.setDiscountDetails(List.of(discount));

        phaseRecurrentService.calculateCumulativeRange(priceRecipe, profilingRequest);

        // todo: assert
    }

}
