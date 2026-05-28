package com.quantfolio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Holding {
    private int        userId;
    private int        stockId;
    private String     symbol;
    private String     companyName;
    private String     sectorName;
    private int        quantity;
    private BigDecimal avgBuyPrice;
    private BigDecimal currentPrice;

    public Holding() {}

    public int        getUserId()      { return userId; }
    public void       setUserId(int v) { this.userId = v; }
    public int        getStockId()     { return stockId; }
    public void       setStockId(int v){ this.stockId = v; }
    public String     getSymbol()      { return symbol; }
    public void       setSymbol(String v) { this.symbol = v; }
    public String     getCompanyName() { return companyName; }
    public void       setCompanyName(String v) { this.companyName = v; }
    public String     getSectorName()  { return sectorName; }
    public void       setSectorName(String v) { this.sectorName = v; }
    public int        getQuantity()    { return quantity; }
    public void       setQuantity(int v){ this.quantity = v; }
    public BigDecimal getAvgBuyPrice() { return avgBuyPrice; }
    public void       setAvgBuyPrice(BigDecimal v) { this.avgBuyPrice = v; }
    public BigDecimal getCurrentPrice(){ return currentPrice; }
    public void       setCurrentPrice(BigDecimal v){ this.currentPrice = v; }

    public BigDecimal getInvestedValue() {
        return avgBuyPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public BigDecimal getCurrentValue() {
        return currentPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public BigDecimal getUnrealizedPnl() {
        return getCurrentValue().subtract(getInvestedValue());
    }
    public BigDecimal getReturnPct() {
        if (avgBuyPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return currentPrice.subtract(avgBuyPrice)
               .divide(avgBuyPrice, 4, RoundingMode.HALF_UP)
               .multiply(BigDecimal.valueOf(100))
               .setScale(2, RoundingMode.HALF_UP);
    }
}
