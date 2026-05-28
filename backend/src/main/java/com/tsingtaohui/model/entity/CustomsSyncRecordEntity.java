package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_customs_sync_record")
public class CustomsSyncRecordEntity extends BaseEntity {

    private String syncNo;
    private Long orderId;
    private String orderNo;
    private String syncNode;
    private String syncLevel;
    private String requestPayload;
    private String responsePayload;
    private String syncStatus;
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime nextRetryAt;

    public String getSyncNo() { return syncNo; }
    public void setSyncNo(String syncNo) { this.syncNo = syncNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getSyncNode() { return syncNode; }
    public void setSyncNode(String syncNode) { this.syncNode = syncNode; }
    public String getSyncLevel() { return syncLevel; }
    public void setSyncLevel(String syncLevel) { this.syncLevel = syncLevel; }
    public String getRequestPayload() { return requestPayload; }
    public void setRequestPayload(String requestPayload) { this.requestPayload = requestPayload; }
    public String getResponsePayload() { return responsePayload; }
    public void setResponsePayload(String responsePayload) { this.responsePayload = responsePayload; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }
}
