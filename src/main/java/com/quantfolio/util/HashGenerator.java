package com.quantfolio.util;

public class HashGenerator {
    public static void main(String[] args) {
        System.out.println("BCrypt Hash Generator");
        System.out.println("====================");

        String admin123 = "admin123";
        String user123 = "user123";

        String adminHash = PasswordUtil.hash(admin123);
        String userHash = PasswordUtil.hash(user123);

        System.out.println("\nFor seed_data.sql:");
        System.out.println("------------------");
        System.out.println("admin123 hash: " + adminHash);
        System.out.println("user123 hash:  " + userHash);

        System.out.println("\nSQL UPDATE statements:");
        System.out.println("---------------------");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE email = 'admin@quantfolio.com';");
        System.out.println("UPDATE users SET password_hash = '" + userHash + "' WHERE email = 'alice@example.com';");
        System.out.println("UPDATE users SET password_hash = '" + userHash + "' WHERE email = 'bob@example.com';");
    }
}
