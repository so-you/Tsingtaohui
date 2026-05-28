package com.tsingtaohui.model.vo;

import java.util.ArrayList;
import java.util.List;

public class OrderEstimateVO {

    private String totalPrice;
    private String totalWeightKg;
    private String totalVolumeM3;
    private String tradeMode;
    private boolean canAutoTrade;
    private List<String> reasons = new ArrayList<>();
    private List<OrderItemVO> items = new ArrayList<>();

    public String getTotalPrice() { return totalPrice; }
    public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
    public String getTotalWeightKg() { return totalWeightKg; }
    public void setTotalWeightKg(String totalWeightKg) { this.totalWeightKg = totalWeightKg; }
    public String getTotalVolumeM3() { return totalVolumeM3; }
    public void setTotalVolumeM3(String totalVolumeM3) { this.totalVolumeM3 = totalVolumeM3; }
    public String getTradeMode() { return tradeMode; }
    public void setTradeMode(String tradeMode) { this.tradeMode = tradeMode; }
    public boolean isCanAutoTrade() { return canAutoTrade; }
    public void setCanAutoTrade(boolean canAutoTrade) { this.canAutoTrade = canAutoTrade; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public List<OrderItemVO> getItems() { return items; }
    public void setItems(List<OrderItemVO> items) { this.items = items; }
}
