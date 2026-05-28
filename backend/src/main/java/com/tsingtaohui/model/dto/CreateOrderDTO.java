package com.tsingtaohui.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderDTO {

    @Valid
    @NotEmpty(message = "Items are required")
    private List<OrderItemDTO> items;

    @NotBlank(message = "Consignee name is required")
    private String consigneeName;

    @NotBlank(message = "Cabin number is required")
    private String cabinNo;

    private String contactInfo;
    private LocalDateTime expectedDeliveryTime;
    private String remark;
    private String shipNo;
    private String shipName;
    private String shipNationality;
    private String imo;
    private String mmsi;
    private String berthOrAnchorage;
    private String targetGps;
    private Long shippingAgentId;
    private String shippingAgentName;

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
    public String getConsigneeName() { return consigneeName; }
    public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }
    public String getCabinNo() { return cabinNo; }
    public void setCabinNo(String cabinNo) { this.cabinNo = cabinNo; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public LocalDateTime getExpectedDeliveryTime() { return expectedDeliveryTime; }
    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) { this.expectedDeliveryTime = expectedDeliveryTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getShipNo() { return shipNo; }
    public void setShipNo(String shipNo) { this.shipNo = shipNo; }
    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }
    public String getShipNationality() { return shipNationality; }
    public void setShipNationality(String shipNationality) { this.shipNationality = shipNationality; }
    public String getImo() { return imo; }
    public void setImo(String imo) { this.imo = imo; }
    public String getMmsi() { return mmsi; }
    public void setMmsi(String mmsi) { this.mmsi = mmsi; }
    public String getBerthOrAnchorage() { return berthOrAnchorage; }
    public void setBerthOrAnchorage(String berthOrAnchorage) { this.berthOrAnchorage = berthOrAnchorage; }
    public String getTargetGps() { return targetGps; }
    public void setTargetGps(String targetGps) { this.targetGps = targetGps; }
    public Long getShippingAgentId() { return shippingAgentId; }
    public void setShippingAgentId(Long shippingAgentId) { this.shippingAgentId = shippingAgentId; }
    public String getShippingAgentName() { return shippingAgentName; }
    public void setShippingAgentName(String shippingAgentName) { this.shippingAgentName = shippingAgentName; }
}
