package com.example.demo.services;

import com.example.demo.models.PriceRecipe;
import com.example.demo.models.ProfilingRequestDTO;

/**
 * The interface Price setting strategy.
 */
public interface ISimplePricingOneOffService {

    /**
     * Calculate price.
     *
     * @param recipe              the recipe
     * @param profilingRequestDTO the profiling request dto
     */
    void calculatePrice(PriceRecipe recipe, ProfilingRequestDTO profilingRequestDTO);
}

