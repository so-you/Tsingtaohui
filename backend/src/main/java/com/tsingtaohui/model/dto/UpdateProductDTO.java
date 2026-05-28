package com.tsingtaohui.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateProductDTO {

    private Long categoryId;

    @NotBlank(message = "Chinese product name is required")
    private String nameZh;

    @NotBlank(message = "English product name is required")
    private String nameEn;

    private String descriptionZh;
    private String descriptionEn;
    private String mainImageUrl;
    private String specification;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.000", inclusive = false, message = "Weight must be greater than zero")
    private BigDecimal weightKg;

    @NotNull(message = "Volume is required")
    @DecimalMin(value = "0.0000", inclusive = false, message = "Volume must be greater than zero")
    private BigDecimal volumeM3;

    private String source;
    private Boolean droneDeliverable;
    private String status;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String nameZh) { this.nameZh = nameZh; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getDescriptionZh() { return descriptionZh; }
    public void setDescriptionZh(String descriptionZh) { this.descriptionZh = descriptionZh; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public BigDecimal getVolumeM3() { return volumeM3; }
    public void setVolumeM3(BigDecimal volumeM3) { this.volumeM3 = volumeM3; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Boolean getDroneDeliverable() { return droneDeliverable; }
    public void setDroneDeliverable(Boolean droneDeliverable) { this.droneDeliverable = droneDeliverable; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
