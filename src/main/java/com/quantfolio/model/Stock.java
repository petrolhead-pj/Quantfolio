package com.quantfolio.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Stock {
    private int        stockId;
    private String     symbol;
    private String     companyName;
    private int        sectorId;
    private String     sectorName;
    private BigDecimal currentPrice;
    private Timestamp  updatedAt;

    public Stock() {}

    public int        getStockId()      { return stockId; }
    public void       setStockId(int v) { this.stockId = v; }
    public String     getSymbol()       { return symbol; }
    public void       setSymbol(String v) { this.symbol = v; }
    public String     getCompanyName()  { return companyName; }
    public void       setCompanyName(String v) { this.companyName = v; }
    public int        getSectorId()     { return sectorId; }
    public void       setSectorId(int v){ this.sectorId = v; }
    public String     getSectorName()   { return sectorName; }
    public void       setSectorName(String v) { this.sectorName = v; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void       setCurrentPrice(BigDecimal v) { this.currentPrice = v; }
    public Timestamp  getUpdatedAt()    { return updatedAt; }
    public void       setUpdatedAt(Timestamp v) { this.updatedAt = v; }
}
