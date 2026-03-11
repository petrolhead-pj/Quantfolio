package com.quantfolio.model;

import java.math.BigDecimal;

public class PortfolioSummary {
    private int        userId;
    private String     userName;
    private int        totalStocks;
    private BigDecimal portfolioValue;
    private BigDecimal totalInvested;
    private BigDecimal unrealizedPnl;
    private BigDecimal pnlPct;

    public PortfolioSummary() {}

    public int        getUserId()        { return userId; }
    public void       setUserId(int v)   { this.userId = v; }
    public String     getUserName()      { return userName; }
    public void       setUserName(String v) { this.userName = v; }
    public int        getTotalStocks()   { return totalStocks; }
    public void       setTotalStocks(int v){ this.totalStocks = v; }
    public BigDecimal getPortfolioValue(){ return portfolioValue; }
    public void       setPortfolioValue(BigDecimal v) { this.portfolioValue = v; }
    public BigDecimal getTotalInvested() { return totalInvested; }
    public void       setTotalInvested(BigDecimal v) { this.totalInvested = v; }
    public BigDecimal getUnrealizedPnl() { return unrealizedPnl; }
    public void       setUnrealizedPnl(BigDecimal v) { this.unrealizedPnl = v; }
    public BigDecimal getPnlPct()        { return pnlPct; }
    public void       setPnlPct(BigDecimal v) { this.pnlPct = v; }
}
