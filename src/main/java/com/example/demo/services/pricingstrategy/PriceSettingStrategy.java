package com.example.demo.services.pricingstrategy;

import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;

/**
 * The interface Price setting strategy.
 */
public interface PriceSettingStrategy {

    /**
     * Calculate price.
     *
     * @param recipe              the recipe
     * @param profilingRequestDTO the profiling request dto
     */
    void calculatePrice(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO);
}

