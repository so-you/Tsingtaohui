package com.tsingtaohui.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CartEstimateDTO {

    @Valid
    @NotEmpty(message = "Items are required")
    private List<OrderItemDTO> items;

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
