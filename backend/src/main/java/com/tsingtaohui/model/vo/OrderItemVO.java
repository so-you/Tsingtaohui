package com.tsingtaohui.model.vo;

public class OrderItemVO {

    private Long id;
    private Long productId;
    private String skuCode;
    private String productNameZh;
    private String productNameEn;
    private String unitPrice;
    private Integer quantity;
    private String unitWeightKg;
    private String unitVolumeM3;
    private String lineAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getProductNameZh() { return productNameZh; }
    public void setProductNameZh(String productNameZh) { this.productNameZh = productNameZh; }
    public String getProductNameEn() { return productNameEn; }
    public void setProductNameEn(String productNameEn) { this.productNameEn = productNameEn; }
    public String getUnitPrice() { return unitPrice; }
    public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnitWeightKg() { return unitWeightKg; }
    public void setUnitWeightKg(String unitWeightKg) { this.unitWeightKg = unitWeightKg; }
    public String getUnitVolumeM3() { return unitVolumeM3; }
    public void setUnitVolumeM3(String unitVolumeM3) { this.unitVolumeM3 = unitVolumeM3; }
    public String getLineAmount() { return lineAmount; }
    public void setLineAmount(String lineAmount) { this.lineAmount = lineAmount; }
}
