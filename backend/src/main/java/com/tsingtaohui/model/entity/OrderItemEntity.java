package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("t_order_item")
public class OrderItemEntity extends BaseEntity {

    private Long orderId;
    private String orderNo;
    private Long productId;
    private String skuCode;
    private String productNameZh;
    private String productNameEn;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal unitWeightKg;
    private BigDecimal unitVolumeM3;
    private BigDecimal lineAmount;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getProductNameZh() { return productNameZh; }
    public void setProductNameZh(String productNameZh) { this.productNameZh = productNameZh; }
    public String getProductNameEn() { return productNameEn; }
    public void setProductNameEn(String productNameEn) { this.productNameEn = productNameEn; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitWeightKg() { return unitWeightKg; }
    public void setUnitWeightKg(BigDecimal unitWeightKg) { this.unitWeightKg = unitWeightKg; }
    public BigDecimal getUnitVolumeM3() { return unitVolumeM3; }
    public void setUnitVolumeM3(BigDecimal unitVolumeM3) { this.unitVolumeM3 = unitVolumeM3; }
    public BigDecimal getLineAmount() { return lineAmount; }
    public void setLineAmount(BigDecimal lineAmount) { this.lineAmount = lineAmount; }
}
