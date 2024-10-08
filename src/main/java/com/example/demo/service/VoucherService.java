package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class VoucherService {

    public void calculateVoucher(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        List<LineItem> lineItems = profilingRequest.getLineItems();
        LocalDate currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();

        for (LineItem item : lineItems) {
            // Convert timestamps to LocalDate for comparison
            LocalDate voucherStartDate = Instant.ofEpochMilli(priceRecipe.getVoucherStartDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate voucherEndDate = Instant.ofEpochMilli(priceRecipe.getVoucherEndDate()).atZone(ZoneId.systemDefault()).toLocalDate();

            // 1.1 Check if currentDate is in between VoucherStartDate + VoucherEndDate
            if (!isDateWithinRange(currentDate, voucherStartDate, voucherEndDate)) {
                continue; // Move to next lineItem if out of date
            }

            // 1.2 Check if the Voucher is already used or not
            if (isVoucherUsed(item)) {
                continue; // Move to next lineItem if voucher is already used
            }

            // 1.3 Execute the formula in PricingCondition & AppliedOn
            if (!Util.isValidFormula(priceRecipe.getPricingCondition(), item.getId()) || !Util.isValidFormula(priceRecipe.getAppliedOn(), item.getId())) {
                continue; // Move to next lineItem if the formula is false
            }

            // Get the latest discount detail for this LineItem and pricing context
            DiscountDetails latestDiscount = Util.findLatestDiscountDetail(item.getId(), priceRecipe.getPriceApplicationON(), profilingRequest);
            // Only apply adjustments if there are existing discounts
            if (latestDiscount != null) {

                // Calculate the adjusted price using the deal strategy and application details from the PriceRecipeRange
                double adjustedPrice = Util.calculateAdjustedPrice(
                        latestDiscount.getAfterAdjustment(),
                        priceRecipe.getDealStrategy(),
                        priceRecipe.getApplicationType(),
                        String.valueOf(priceRecipe.getApplicationValue())
                );

                // Determine the next sequence number for the discount
                int nextSequence = latestDiscount.getSequence() + 1;

                // Create and add a new DiscountDetails entry to the profiling request
                Util.createAndAddDiscountDetails(item, latestDiscount.getAfterAdjustment(), adjustedPrice, nextSequence, priceRecipe, profilingRequest);

                createVoucherAudit(item, priceRecipe);
            }
        }
    }


    private boolean isVoucherUsed(LineItem item) {
        return true;
    }

    private boolean isDateWithinRange(LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        return (currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) &&
                (currentDate.isEqual(endDate) || currentDate.isBefore(endDate));
    }

    private void createVoucherAudit(LineItem item, PriceRecipe priceRecipe) {
        VoucherAudit voucherAudit = new VoucherAudit();
        voucherAudit.setVoucherCode(priceRecipe.getVoucherCode()); // Example value, replace as needed
        voucherAudit.setStartDate(priceRecipe.getVoucherStartDate());
        voucherAudit.setEndDate(priceRecipe.getVoucherEndDate());
        voucherAudit.setLineItemId(item.getId());
        voucherAudit.setAccountId("");
        voucherAudit.setProductConfigurationId(item.getProductId());

        // call the service to save VoucherAudit to DB
    }
}
