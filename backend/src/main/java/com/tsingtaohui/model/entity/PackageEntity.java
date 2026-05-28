package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("t_package")
public class PackageEntity extends BaseEntity {

    private String packageNo;
    private Long orderId;
    private String orderNo;
    private BigDecimal actualWeightKg;
    private BigDecimal actualVolumeM3;
    private String packageStatus;

    public String getPackageNo() { return packageNo; }
    public void setPackageNo(String packageNo) { this.packageNo = packageNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getActualWeightKg() { return actualWeightKg; }
    public void setActualWeightKg(BigDecimal actualWeightKg) { this.actualWeightKg = actualWeightKg; }
    public BigDecimal getActualVolumeM3() { return actualVolumeM3; }
    public void setActualVolumeM3(BigDecimal actualVolumeM3) { this.actualVolumeM3 = actualVolumeM3; }
    public String getPackageStatus() { return packageStatus; }
    public void setPackageStatus(String packageStatus) { this.packageStatus = packageStatus; }
}
