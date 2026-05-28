package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_inventory")
public class InventoryEntity extends BaseEntity {

    private Long warehouseId;
    private String locationCode;
    private String skuCode;
    private String batchNo;
    private Integer availableQty;
    private Integer lockedQty;
    private Integer outboundQty;
    private Integer version;

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Integer getAvailableQty() { return availableQty; }
    public void setAvailableQty(Integer availableQty) { this.availableQty = availableQty; }
    public Integer getLockedQty() { return lockedQty; }
    public void setLockedQty(Integer lockedQty) { this.lockedQty = lockedQty; }
    public Integer getOutboundQty() { return outboundQty; }
    public void setOutboundQty(Integer outboundQty) { this.outboundQty = outboundQty; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
