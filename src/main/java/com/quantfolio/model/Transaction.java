package com.quantfolio.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int        transactionId;
    private int        userId;
    private int        stockId;
    private String     symbol;
    private String     companyName;
    private String     type;          // BUY | SELL
    private int        quantity;
    private BigDecimal price;
    private Timestamp  transactionDate;
    private String     notes;

    public Transaction() {}

    public int        getTransactionId()       { return transactionId; }
    public void       setTransactionId(int v)  { this.transactionId = v; }
    public int        getUserId()              { return userId; }
    public void       setUserId(int v)         { this.userId = v; }
    public int        getStockId()             { return stockId; }
    public void       setStockId(int v)        { this.stockId = v; }
    public String     getSymbol()              { return symbol; }
    public void       setSymbol(String v)      { this.symbol = v; }
    public String     getCompanyName()         { return companyName; }
    public void       setCompanyName(String v) { this.companyName = v; }
    public String     getType()                { return type; }
    public void       setType(String v)        { this.type = v; }
    public int        getQuantity()            { return quantity; }
    public void       setQuantity(int v)       { this.quantity = v; }
    public BigDecimal getPrice()               { return price; }
    public void       setPrice(BigDecimal v)   { this.price = v; }
    public Timestamp  getTransactionDate()     { return transactionDate; }
    public void       setTransactionDate(Timestamp v) { this.transactionDate = v; }
    public String     getNotes()               { return notes; }
    public void       setNotes(String v)       { this.notes = v; }
    public BigDecimal getTotalValue()          { return price.multiply(BigDecimal.valueOf(quantity)); }
}
