-- ============================================================
--  Quantfolio — Database Schema
--  Version: 1.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS quantfolio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quantfolio;

-- -----------------------------------------------
-- Sectors
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS sectors (
    sector_id   INT AUTO_INCREMENT PRIMARY KEY,
    sector_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- -----------------------------------------------
-- Users
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    email         VARCHAR(255)        NOT NULL UNIQUE,
    password_hash VARCHAR(255)        NOT NULL,
    role          ENUM('admin','user') NOT NULL DEFAULT 'user',
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -----------------------------------------------
-- Stocks
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS stocks (
    stock_id      INT AUTO_INCREMENT PRIMARY KEY,
    symbol        VARCHAR(10)      NOT NULL UNIQUE,
    company_name  VARCHAR(255)     NOT NULL,
    sector_id     INT              NOT NULL,
    current_price DECIMAL(10, 2)   NOT NULL DEFAULT 0.00,
    updated_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_sector FOREIGN KEY (sector_id) REFERENCES sectors(sector_id)
);

-- -----------------------------------------------
-- Transactions
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT              NOT NULL,
    stock_id         INT              NOT NULL,
    type             ENUM('BUY','SELL') NOT NULL,
    quantity         INT              NOT NULL CHECK (quantity > 0),
    price            DECIMAL(10, 2)   NOT NULL CHECK (price > 0),
    transaction_date TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes            TEXT,
    CONSTRAINT fk_txn_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
    CONSTRAINT fk_txn_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------
-- Portfolio Holdings  (maintained by triggers)
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS portfolio_holdings (
    holding_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT            NOT NULL,
    stock_id      INT            NOT NULL,
    quantity      INT            NOT NULL DEFAULT 0,
    avg_buy_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_holding (user_id, stock_id),
    CONSTRAINT fk_hold_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
    CONSTRAINT fk_hold_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------
-- Price History
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS price_history (
    history_id    INT AUTO_INCREMENT PRIMARY KEY,
    stock_id      INT            NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    recorded_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ph_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------
-- Watchlist
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS watchlist (
    watchlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT       NOT NULL,
    stock_id     INT       NOT NULL,
    added_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_watch (user_id, stock_id),
    CONSTRAINT fk_wl_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
    CONSTRAINT fk_wl_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------
-- Alerts
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS alerts (
    alert_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT            NOT NULL,
    stock_id     INT            NOT NULL,
    alert_type   ENUM('ABOVE','BELOW') NOT NULL,
    target_price DECIMAL(10, 2) NOT NULL,
    is_active    TINYINT(1)     NOT NULL DEFAULT 1,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
    CONSTRAINT fk_alert_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------
-- Analytics Cache
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS analytics_cache (
    cache_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    cache_key   VARCHAR(100) NOT NULL,
    cache_value TEXT         NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    UNIQUE KEY uq_cache (user_id, cache_key),
    CONSTRAINT fk_cache_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -----------------------------------------------
-- Audit Log
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    log_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,
    action     VARCHAR(100) NOT NULL,
    detail     TEXT,
    ip_address VARCHAR(45),
    logged_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
