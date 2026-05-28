package com.tsingtaohui.model.vo;

import java.time.LocalDateTime;

public class AdminProductVO {

    private Long id;
    private String skuCode;
    private Long categoryId;
    private String nameZh;
    private String nameEn;
    private String price;
    private String weightKg;
    private String volumeM3;
    private String source;
    private boolean droneDeliverable;
    private String status;
    private int availableQty;
    private int lockedQty;
    private int outboundQty;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String nameZh) { this.nameZh = nameZh; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getWeightKg() { return weightKg; }
    public void setWeightKg(String weightKg) { this.weightKg = weightKg; }
    public String getVolumeM3() { return volumeM3; }
    public void setVolumeM3(String volumeM3) { this.volumeM3 = volumeM3; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public boolean isDroneDeliverable() { return droneDeliverable; }
    public void setDroneDeliverable(boolean droneDeliverable) { this.droneDeliverable = droneDeliverable; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getAvailableQty() { return availableQty; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }
    public int getLockedQty() { return lockedQty; }
    public void setLockedQty(int lockedQty) { this.lockedQty = lockedQty; }
    public int getOutboundQty() { return outboundQty; }
    public void setOutboundQty(int outboundQty) { this.outboundQty = outboundQty; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
