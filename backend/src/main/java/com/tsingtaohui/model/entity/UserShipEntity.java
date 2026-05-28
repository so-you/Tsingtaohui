package com.tsingtaohui.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_user_ship")
public class UserShipEntity extends BaseEntity {

    private Long userId;
    private String shipNo;
    private String shipName;
    private String shipNationality;
    private String imo;
    private String mmsi;
    private Integer isDefault;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getShipNo() { return shipNo; }
    public void setShipNo(String shipNo) { this.shipNo = shipNo; }
    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }
    public String getShipNationality() { return shipNationality; }
    public void setShipNationality(String shipNationality) { this.shipNationality = shipNationality; }
    public String getImo() { return imo; }
    public void setImo(String imo) { this.imo = imo; }
    public String getMmsi() { return mmsi; }
    public void setMmsi(String mmsi) { this.mmsi = mmsi; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
}
