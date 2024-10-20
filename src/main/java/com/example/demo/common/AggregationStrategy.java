package com.example.demo.common;

public enum AggregationStrategy {
    SUM,          // Sum all matching items' quantities
    MAX,          // Use the highest quantity from matching items
    FIRST_MATCH   // Use the first matched item only
}