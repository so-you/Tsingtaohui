package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("t_drone")
public class DroneEntity extends BaseEntity {

    private String droneCode;
    private String model;
    private String flightNo;
    private BigDecimal maxPayloadKg;
    private BigDecimal maxVolumeM3;
    private BigDecimal maxRangeKm;
    private String deliverableCategories;
    private String status;

    public String getDroneCode() { return droneCode; }
    public void setDroneCode(String droneCode) { this.droneCode = droneCode; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }
    public BigDecimal getMaxPayloadKg() { return maxPayloadKg; }
    public void setMaxPayloadKg(BigDecimal maxPayloadKg) { this.maxPayloadKg = maxPayloadKg; }
    public BigDecimal getMaxVolumeM3() { return maxVolumeM3; }
    public void setMaxVolumeM3(BigDecimal maxVolumeM3) { this.maxVolumeM3 = maxVolumeM3; }
    public BigDecimal getMaxRangeKm() { return maxRangeKm; }
    public void setMaxRangeKm(BigDecimal maxRangeKm) { this.maxRangeKm = maxRangeKm; }
    public String getDeliverableCategories() { return deliverableCategories; }
    public void setDeliverableCategories(String deliverableCategories) { this.deliverableCategories = deliverableCategories; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
