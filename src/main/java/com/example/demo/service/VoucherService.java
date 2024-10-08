package com.example.demo.service;

import com.example.demo.Util;
import com.example.demo.models.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class VoucherService {

    public void calculateVoucher(PriceRecipe priceRecipe, ProfilingRequestDTO profilingRequest) {
        Voucher voucher = priceRecipe.getVoucher();
        if(voucher != null) {
            if(voucher.getIsUsed()){
                return;
            }

            // Convert timestamps to LocalDate for comparison
            LocalDate currentDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate voucherStartDate = Instant.ofEpochMilli(voucher.getStartDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate voucherEndDate = Instant.ofEpochMilli(voucher.getEndDate()).atZone(ZoneId.systemDefault()).toLocalDate();

            if (!isDateWithinRange(currentDate, voucherStartDate, voucherEndDate)) {
                return;
            }

            List<LineItem> lineItems = profilingRequest.getLineItems();
            for (LineItem item : lineItems) {

                // Execute the formula in PricingCondition & AppliedOn
                if (!Util.isValidFormula(priceRecipe.getPricingCondition(), item) || !Util.isValidFormula(priceRecipe.getAppliedOn(), item)) {
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

                    // create voucherAudit to keep track the voucher usage then call the function to save to DB
                    createVoucherAudit(item, voucher);

                    // set the voucher is used to true => call the function save the recipe to DB
                    updateVoucherState(priceRecipe, voucher);
                }
            }
        }
    }

    private void updateVoucherState(PriceRecipe priceRecipe, Voucher voucher) {
        voucher.setIsUsed(true);
        priceRecipe.setVoucher(voucher);

        //call the service to save PriceRecipe to db
    }

    private boolean isDateWithinRange(LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        return (currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) &&
                (currentDate.isEqual(endDate) || currentDate.isBefore(endDate));
    }

    private void createVoucherAudit(LineItem item, Voucher voucher) {
        VoucherAudit voucherAudit = new VoucherAudit();
        voucherAudit.setVoucherCode(voucher.getCode()); // Example value, replace as needed
        voucherAudit.setStartDate(voucher.getStartDate());
        voucherAudit.setEndDate(voucher.getEndDate());
        voucherAudit.setLineItemId(item.getId());
        voucherAudit.setAccountId("");
        voucherAudit.setProductConfigurationId(item.getProductId());

        // call the service to save VoucherAudit to DB
    }
}
