package com.example.demo.enums;

import com.example.demo.models.LineItem;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.ObjDoubleConsumer;

@Getter
public enum LineItemPricesSetter {
    LIST_PRICE("listPrice", LineItem::setListPrice),
    REFERENCE_PRICE("referencePrice", LineItem::setReferencePrice),
    CONTRACT_PRICE("contractPrice", LineItem::setContractPrice),
    NET_PRICE("netPrice", LineItem::setNetPrice),
    UNIT_PRICE("unitPrice", LineItem::setUnitPrice);

    private final String fieldName;
    private final ObjDoubleConsumer<LineItem> setter;

    LineItemPricesSetter(String fieldName, ObjDoubleConsumer<LineItem> setter) {
        this.fieldName = fieldName;
        this.setter = setter;
    }

    public static ObjDoubleConsumer<LineItem> getSetterByFieldName(String fieldName) {
        return Arrays.stream(LineItemPricesSetter.values())
                .filter(mapping -> mapping.getFieldName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown field: " + fieldName))
                .getSetter();
    }
}
