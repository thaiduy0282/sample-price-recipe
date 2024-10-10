package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.DiscountDetails;
import com.example.demo.models.LineItem;
import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PhaseRecurrentService {

    public void calculateCumulativeRange(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        List<LineItem> lineItems = profilingRequest.getLineItems();
        Map<String, List<LineItem>> groupedByProductId = lineItems.stream()
                .collect(Collectors.groupingBy(LineItem::getProductId));

        groupedByProductId.forEach((productId, items) -> {

            items.sort(Comparator.comparing(LineItem::getStartDate));

            Map<String, List<LineItem>> groupedByTimeDimension = items.stream()
                    .collect(Collectors.groupingBy(LineItem::getTimeDimensionName));

            List<Map.Entry<String, List<LineItem>>> timeDimensionEntries = new ArrayList<>(groupedByTimeDimension.entrySet());
            timeDimensionEntries.sort(Map.Entry.comparingByKey());

            for (int i = 1; i < timeDimensionEntries.size(); i++) {
                List<LineItem> previousGroupItems = timeDimensionEntries.get(i - 1).getValue();
                List<LineItem> currentGroupItems = timeDimensionEntries.get(i).getValue();
                for (LineItem item : currentGroupItems) {
                    DiscountDetails latestDiscount = Util.findLatestDiscountDetail(previousGroupItems.getFirst().getId(),
                            priceRecipe.getPriceApplicationON(), profilingRequest);
                    if (latestDiscount != null) {
                        double adjustedPrice = Util.calculateAdjustedPrice(
                                latestDiscount.getAfterAdjustment(),
                                priceRecipe.getDealStrategy(),
                                priceRecipe.getApplicationType(),
                                String.valueOf(priceRecipe.getApplicationValue())
                        );
                        int nextSequence = latestDiscount.getSequence() + 1;
                        Util.createAndAddDiscountDetails(item, latestDiscount.getAfterAdjustment(),
                                adjustedPrice, nextSequence, priceRecipe, profilingRequest);
                    }
                }
            }
        });
    }

}
