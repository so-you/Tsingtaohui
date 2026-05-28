package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_order")
public class OrderEntity extends BaseEntity {

    private String orderNo;
    private Long userId;
    private BigDecimal totalPrice;
    private BigDecimal totalWeightKg;
    private BigDecimal totalVolumeM3;
    private String tradeMode;
    private String orderStatus;
    private String warehouseStatus;
    private String deliveryStatus;
    private String customsSyncStatus;
    private String consigneeName;
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
    private LocalDateTime completedAt;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getTotalWeightKg() { return totalWeightKg; }
    public void setTotalWeightKg(BigDecimal totalWeightKg) { this.totalWeightKg = totalWeightKg; }
    public BigDecimal getTotalVolumeM3() { return totalVolumeM3; }
    public void setTotalVolumeM3(BigDecimal totalVolumeM3) { this.totalVolumeM3 = totalVolumeM3; }
    public String getTradeMode() { return tradeMode; }
    public void setTradeMode(String tradeMode) { this.tradeMode = tradeMode; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getWarehouseStatus() { return warehouseStatus; }
    public void setWarehouseStatus(String warehouseStatus) { this.warehouseStatus = warehouseStatus; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public String getCustomsSyncStatus() { return customsSyncStatus; }
    public void setCustomsSyncStatus(String customsSyncStatus) { this.customsSyncStatus = customsSyncStatus; }
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
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
