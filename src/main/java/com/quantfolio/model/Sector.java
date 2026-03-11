package com.quantfolio.model;

import java.math.BigDecimal;

public class Sector {
    private int        sectorId;
    private String     sectorName;
    private String     description;
    private BigDecimal sectorValue;
    private BigDecimal allocationPct;

    public int        getSectorId()        { return sectorId; }
    public void       setSectorId(int v)   { this.sectorId = v; }
    public String     getSectorName()      { return sectorName; }
    public void       setSectorName(String v) { this.sectorName = v; }
    public String     getDescription()     { return description; }
    public void       setDescription(String v) { this.description = v; }
    public BigDecimal getSectorValue()     { return sectorValue; }
    public void       setSectorValue(BigDecimal v) { this.sectorValue = v; }
    public BigDecimal getAllocationPct()   { return allocationPct; }
    public void       setAllocationPct(BigDecimal v) { this.allocationPct = v; }
}
