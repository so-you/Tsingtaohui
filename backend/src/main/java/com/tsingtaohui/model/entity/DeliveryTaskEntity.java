package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_delivery_task")
public class DeliveryTaskEntity extends BaseEntity {

    private String taskNo;
    private Long orderId;
    private String orderNo;
    private String packageNo;
    private Long warehouseId;
    private String targetShipNo;
    private String targetLocation;
    private Long droneId;
    private String droneCode;
    private String taskStatus;
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;

    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getPackageNo() { return packageNo; }
    public void setPackageNo(String packageNo) { this.packageNo = packageNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getTargetShipNo() { return targetShipNo; }
    public void setTargetShipNo(String targetShipNo) { this.targetShipNo = targetShipNo; }
    public String getTargetLocation() { return targetLocation; }
    public void setTargetLocation(String targetLocation) { this.targetLocation = targetLocation; }
    public Long getDroneId() { return droneId; }
    public void setDroneId(Long droneId) { this.droneId = droneId; }
    public String getDroneCode() { return droneCode; }
    public void setDroneCode(String droneCode) { this.droneCode = droneCode; }
    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }
    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }
}
