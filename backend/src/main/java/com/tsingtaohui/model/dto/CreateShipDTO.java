package com.tsingtaohui.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateShipDTO {

    @NotBlank(message = "Ship number is required")
    private String shipNo;
    private String shipName;
    @NotBlank(message = "Ship nationality is required")
    private String shipNationality;
    private String imo;
    private String mmsi;
    private String currentBerth;
    private String currentAnchorage;
    private String targetGps;
    private String locationSource;

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
}
