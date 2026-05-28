package com.tsingtaohui.model.vo;

import java.time.LocalDateTime;

public class ShipAdminVO {

    private Long id;
    private String shipNo;
    private String shipName;
    private String shipNationality;
    private String imo;
    private String mmsi;
    private String currentBerth;
    private String currentAnchorage;
    private String targetGps;
    private String locationSource;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getCurrentBerth() { return currentBerth; }
    public void setCurrentBerth(String currentBerth) { this.currentBerth = currentBerth; }
    public String getCurrentAnchorage() { return currentAnchorage; }
    public void setCurrentAnchorage(String currentAnchorage) { this.currentAnchorage = currentAnchorage; }
    public String getTargetGps() { return targetGps; }
    public void setTargetGps(String targetGps) { this.targetGps = targetGps; }
    public String getLocationSource() { return locationSource; }
    public void setLocationSource(String locationSource) { this.locationSource = locationSource; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
