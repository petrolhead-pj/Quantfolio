package com.quantfolio.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private int       userId;
    private String    name;
    private String    email;
    private String    passwordHash;
    private String    role;
    private Timestamp createdAt;

    public User() {}

    public User(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name   = name;
        this.email  = email;
        this.role   = role;
    }

    // Getters & Setters
    public int       getUserId()       { return userId; }
    public void      setUserId(int v)  { this.userId = v; }
    public String    getName()         { return name; }
    public void      setName(String v) { this.name = v; }
    public String    getEmail()        { return email; }
    public void      setEmail(String v){ this.email = v; }
    public String    getPasswordHash() { return passwordHash; }
    public void      setPasswordHash(String v) { this.passwordHash = v; }
    public String    getRole()         { return role; }
    public void      setRole(String v) { this.role = v; }
    public Timestamp getCreatedAt()    { return createdAt; }
    public void      setCreatedAt(Timestamp v) { this.createdAt = v; }
    public boolean   isAdmin()         { return "admin".equalsIgnoreCase(role); }
}
