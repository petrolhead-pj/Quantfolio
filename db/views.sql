-- ============================================================
--  Quantfolio — Views
-- ============================================================
USE quantfolio;

-- -----------------------------------------------
-- View: Portfolio Summary per User
-- -----------------------------------------------
CREATE OR REPLACE VIEW portfolio_summary_view AS
SELECT
    u.user_id,
    u.name                                                        AS user_name,
    COUNT(DISTINCT ph.stock_id)                                   AS total_stocks,
    SUM(ph.quantity * s.current_price)                            AS portfolio_value,
    SUM(ph.quantity * ph.avg_buy_price)                           AS total_invested,
    SUM(ph.quantity * (s.current_price - ph.avg_buy_price))       AS unrealized_pnl,
    ROUND(
        SUM(ph.quantity * (s.current_price - ph.avg_buy_price))
        / NULLIF(SUM(ph.quantity * ph.avg_buy_price), 0) * 100
    , 2)                                                          AS pnl_pct
FROM portfolio_holdings ph
JOIN users  u ON ph.user_id  = u.user_id
JOIN stocks s ON ph.stock_id = s.stock_id
GROUP BY u.user_id, u.name;

-- -----------------------------------------------
-- View: Holdings Detail per User
-- -----------------------------------------------
CREATE OR REPLACE VIEW holdings_detail_view AS
SELECT
    ph.user_id,
    s.symbol,
    s.company_name,
    sec.sector_name,
    ph.quantity,
    ph.avg_buy_price,
    s.current_price,
    ph.quantity * ph.avg_buy_price                               AS invested_value,
    ph.quantity * s.current_price                                AS current_value,
    ph.quantity * (s.current_price - ph.avg_buy_price)          AS unrealized_pnl,
    ROUND(
        (s.current_price - ph.avg_buy_price)
        / ph.avg_buy_price * 100
    , 2)                                                         AS return_pct
FROM portfolio_holdings ph
JOIN stocks  s   ON ph.stock_id  = s.stock_id
JOIN sectors sec ON s.sector_id  = sec.sector_id;

-- -----------------------------------------------
-- View: Sector Allocation per User
-- -----------------------------------------------
CREATE OR REPLACE VIEW sector_allocation_view AS
SELECT
    ph.user_id,
    sec.sector_name,
    SUM(ph.quantity * s.current_price)                           AS sector_value,
    ROUND(
        SUM(ph.quantity * s.current_price)
        / SUM(SUM(ph.quantity * s.current_price)) OVER (PARTITION BY ph.user_id) * 100
    , 2)                                                         AS allocation_pct
FROM portfolio_holdings ph
JOIN stocks  s   ON ph.stock_id = s.stock_id
JOIN sectors sec ON s.sector_id = sec.sector_id
GROUP BY ph.user_id, sec.sector_id, sec.sector_name;

-- -----------------------------------------------
-- View: Top Performing Stocks per User
-- -----------------------------------------------
CREATE OR REPLACE VIEW top_performers_view AS
SELECT
    ph.user_id,
    s.symbol,
    s.company_name,
    ph.avg_buy_price,
    s.current_price,
    ROUND((s.current_price - ph.avg_buy_price) / ph.avg_buy_price * 100, 2) AS return_pct
FROM portfolio_holdings ph
JOIN stocks s ON ph.stock_id = s.stock_id
ORDER BY return_pct DESC;

-- -----------------------------------------------
-- View: Monthly Transaction Activity per User
-- -----------------------------------------------
CREATE OR REPLACE VIEW monthly_activity_view AS
SELECT
    user_id,
    YEAR(transaction_date)  AS yr,
    MONTH(transaction_date) AS mo,
    DATE_FORMAT(transaction_date, '%b %Y') AS month_label,
    SUM(CASE WHEN type = 'BUY'  THEN quantity * price ELSE 0 END) AS buy_value,
    SUM(CASE WHEN type = 'SELL' THEN quantity * price ELSE 0 END) AS sell_value,
    COUNT(*)                                                        AS txn_count
FROM transactions
GROUP BY user_id, yr, mo, month_label
ORDER BY yr, mo;
