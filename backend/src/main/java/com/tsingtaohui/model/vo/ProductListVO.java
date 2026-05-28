package com.tsingtaohui.model.vo;

public class ProductListVO {

    private Long id;
    private String skuCode;
    private String nameZh;
    private String nameEn;
    private String price;
    private String mainImageUrl;
    private int availableQty;
    private boolean droneDeliverable;
    private String weightKg;
    private String volumeM3;

    public ProductListVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public boolean isDroneDeliverable() {
        return droneDeliverable;
    }

    public void setDroneDeliverable(boolean droneDeliverable) {
        this.droneDeliverable = droneDeliverable;
    }

    public String getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(String weightKg) {
        this.weightKg = weightKg;
    }

    public String getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(String volumeM3) {
        this.volumeM3 = volumeM3;
    }
}
