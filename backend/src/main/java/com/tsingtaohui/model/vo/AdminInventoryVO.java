package com.tsingtaohui.model.vo;

import java.time.LocalDateTime;

public class AdminInventoryVO {

    private Long id;
    private Long warehouseId;
    private String locationCode;
    private String skuCode;
    private String productNameZh;
    private String productNameEn;
    private String batchNo;
    private int availableQty;
    private int lockedQty;
    private int outboundQty;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getProductNameZh() { return productNameZh; }
    public void setProductNameZh(String productNameZh) { this.productNameZh = productNameZh; }
    public String getProductNameEn() { return productNameEn; }
    public void setProductNameEn(String productNameEn) { this.productNameEn = productNameEn; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public int getAvailableQty() { return availableQty; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }
    public int getLockedQty() { return lockedQty; }
    public void setLockedQty(int lockedQty) { this.lockedQty = lockedQty; }
    public int getOutboundQty() { return outboundQty; }
    public void setOutboundQty(int outboundQty) { this.outboundQty = outboundQty; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
