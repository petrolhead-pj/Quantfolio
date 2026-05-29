-- ============================================================
--  Quantfolio — Advanced Analytical SQL Queries
-- ============================================================
USE quantfolio;

-- -----------------------------------------------
-- 1. Portfolio Diversification Analysis
--    Sector-wise allocation with window functions
-- -----------------------------------------------
SELECT
    sec.sector_name,
    SUM(ph.quantity * s.current_price)                            AS sector_value,
    ROUND(
        SUM(ph.quantity * s.current_price)
        / SUM(SUM(ph.quantity * s.current_price)) OVER () * 100
    , 2)                                                          AS allocation_pct,
    RANK() OVER (ORDER BY SUM(ph.quantity * s.current_price) DESC) AS sector_rank
FROM portfolio_holdings ph
JOIN stocks  s   ON ph.stock_id = s.stock_id
JOIN sectors sec ON s.sector_id = sec.sector_id
WHERE ph.user_id = ?   -- parameterised
GROUP BY sec.sector_id, sec.sector_name;

-- -----------------------------------------------
-- 2. Top 5 Performing Stocks (by return %)
-- -----------------------------------------------
SELECT
    s.symbol,
    s.company_name,
    ph.avg_buy_price,
    s.current_price,
    ROUND((s.current_price - ph.avg_buy_price) / ph.avg_buy_price * 100, 2) AS return_pct,
    RANK() OVER (ORDER BY
        (s.current_price - ph.avg_buy_price) / ph.avg_buy_price DESC
    ) AS perf_rank
FROM portfolio_holdings ph
JOIN stocks s ON ph.stock_id = s.stock_id
WHERE ph.user_id = ?
ORDER BY return_pct DESC
LIMIT 5;

-- -----------------------------------------------
-- 3. Monthly Investment Trend (last 12 months)
-- -----------------------------------------------
SELECT
    DATE_FORMAT(transaction_date, '%b %Y') AS month_label,
    SUM(CASE WHEN type = 'BUY'  THEN quantity * price ELSE 0 END) AS buy_total,
    SUM(CASE WHEN type = 'SELL' THEN quantity * price ELSE 0 END) AS sell_total,
    COUNT(*)                                                        AS transactions
FROM transactions
WHERE user_id = ?
  AND transaction_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY YEAR(transaction_date), MONTH(transaction_date), month_label
ORDER BY YEAR(transaction_date), MONTH(transaction_date);

-- -----------------------------------------------
-- 4. Realized P&L — Matched BUY/SELL via FIFO
-- -----------------------------------------------
SELECT
    s.symbol,
    SUM(CASE WHEN t.type = 'SELL' THEN t.quantity * t.price ELSE 0 END) AS total_proceeds,
    SUM(CASE WHEN t.type = 'BUY'  THEN t.quantity * t.price ELSE 0 END) AS total_cost,
    SUM(CASE WHEN t.type = 'SELL' THEN t.quantity * t.price ELSE 0 END)
    - SUM(CASE WHEN t.type = 'BUY' THEN t.quantity * t.price ELSE 0 END) AS realized_pnl
FROM transactions t
JOIN stocks s ON t.stock_id = s.stock_id
WHERE t.user_id = ?
GROUP BY s.stock_id, s.symbol
HAVING realized_pnl != 0;

-- -----------------------------------------------
-- 5. Portfolio Value Over Time (using price history)
-- -----------------------------------------------
SELECT
    DATE_FORMAT(ph_hist.recorded_at, '%Y-%m') AS month,
    SUM(hold.quantity * ph_hist.price)        AS portfolio_value
FROM portfolio_holdings hold
JOIN (
    SELECT stock_id, MAX(recorded_at) AS max_date,
           DATE_FORMAT(recorded_at, '%Y-%m') AS month_key
    FROM price_history
    GROUP BY stock_id, month_key
) latest ON hold.stock_id = latest.stock_id
JOIN price_history ph_hist
    ON ph_hist.stock_id   = latest.stock_id
   AND ph_hist.recorded_at = latest.max_date
WHERE hold.user_id = ?
GROUP BY month
ORDER BY month;

-- -----------------------------------------------
-- 6. Risk Concentration — Stocks > 15% of portfolio
-- -----------------------------------------------
SELECT
    s.symbol,
    s.company_name,
    ph.quantity * s.current_price                                  AS stock_value,
    ROUND(
        ph.quantity * s.current_price
        / SUM(ph.quantity * s.current_price) OVER () * 100
    , 2)                                                           AS weight_pct
FROM portfolio_holdings ph
JOIN stocks s ON ph.stock_id = s.stock_id
WHERE ph.user_id = ?
HAVING weight_pct > 15
ORDER BY weight_pct DESC;
