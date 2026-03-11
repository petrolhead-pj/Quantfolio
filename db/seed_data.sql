-- ============================================================
--  Quantfolio — Seed Data
-- ============================================================
USE quantfolio;

-- Sectors
INSERT INTO sectors (sector_name, description) VALUES
('Technology',      'Software, hardware, semiconductors, and IT services'),
('Finance',         'Banks, insurance, asset management, and fintech'),
('Healthcare',      'Pharmaceuticals, biotech, hospitals, and medical devices'),
('Consumer Goods',  'Retail, food, beverages, and household products'),
('Energy',          'Oil, gas, renewables, and utilities'),
('Industrials',     'Manufacturing, aerospace, defense, and construction'),
('Telecommunications', 'Wireless carriers, cable, and internet providers'),
('Real Estate',     'REITs, commercial, and residential real estate');

-- Stocks
INSERT INTO stocks (symbol, company_name, sector_id, current_price) VALUES
('AAPL',  'Apple Inc.',                    1, 189.50),
('MSFT',  'Microsoft Corporation',         1, 415.20),
('GOOGL', 'Alphabet Inc.',                 1, 175.80),
('AMZN',  'Amazon.com Inc.',               1, 198.40),
('NVDA',  'NVIDIA Corporation',            1, 875.60),
('META',  'Meta Platforms Inc.',           1, 520.30),
('JPM',   'JPMorgan Chase & Co.',          2, 198.70),
('BAC',   'Bank of America Corp.',         2,  38.20),
('GS',    'Goldman Sachs Group Inc.',      2, 460.10),
('JNJ',   'Johnson & Johnson',             3, 152.30),
('PFE',   'Pfizer Inc.',                   3,  27.80),
('UNH',   'UnitedHealth Group Inc.',       3, 512.40),
('TSLA',  'Tesla Inc.',                    1, 195.20),
('XOM',   'Exxon Mobil Corporation',       5,  98.60),
('CVX',   'Chevron Corporation',           5, 153.40),
('WMT',   'Walmart Inc.',                  4,  66.80),
('PG',    'Procter & Gamble Co.',          4, 162.50),
('T',     'AT&T Inc.',                     7,  17.20),
('VZ',    'Verizon Communications Inc.',   7,  40.10),
('AMT',   'American Tower Corporation',    8, 185.30);

-- Demo Users  (passwords are BCrypt hashes)
-- admin123  => $2a$12$...
-- user123   => $2a$12$...
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User',  'admin@quantfolio.com', '$2a$12$hBSqUTqZvkJ3v3JYO.jLVOqp/gT2R1vR0.x9kpZ7M8N4uY1wQ6CJa', 'admin'),
('Alice Sharma','alice@example.com',    '$2a$12$XtFVRh5y0wNrOpJeREfbLuT.rq3bV9KJ6e7Y4jNk1sP2ZmB8eGuOi', 'user'),
('Bob Kumar',   'bob@example.com',      '$2a$12$XtFVRh5y0wNrOpJeREfbLuT.rq3bV9KJ6e7Y4jNk1sP2ZmB8eGuOi', 'user');

-- Sample Transactions for Alice (user_id = 2)
INSERT INTO transactions (user_id, stock_id, type, quantity, price, transaction_date) VALUES
(2, 1,  'BUY',  10, 150.00, '2023-01-15 10:00:00'),
(2, 2,  'BUY',   5, 280.00, '2023-02-10 11:30:00'),
(2, 5,  'BUY',   8, 420.00, '2023-03-05 09:45:00'),
(2, 7,  'BUY',  15, 145.00, '2023-04-20 14:00:00'),
(2, 10, 'BUY',  10, 165.00, '2023-05-12 10:15:00'),
(2, 1,  'BUY',   5, 175.00, '2023-07-18 09:30:00'),
(2, 13, 'BUY',  20, 240.00, '2023-08-01 10:00:00'),
(2, 5,  'BUY',   4, 480.00, '2023-09-10 11:00:00'),
(2, 2,  'SELL',  2, 330.00, '2023-10-05 14:30:00'),
(2, 14, 'BUY',  12,  95.00, '2023-11-20 09:00:00'),
(2, 16, 'BUY',  25,  58.00, '2024-01-08 10:30:00'),
(2, 1,  'SELL',  5, 185.00, '2024-02-14 13:00:00'),
(2, 6,  'BUY',   6, 380.00, '2024-03-01 09:00:00'),
(2, 17, 'BUY',  10, 155.00, '2024-04-10 11:00:00');

-- Sample Transactions for Bob (user_id = 3)
INSERT INTO transactions (user_id, stock_id, type, quantity, price, transaction_date) VALUES
(3, 3,  'BUY',   8, 130.00, '2023-03-01 09:00:00'),
(3, 4,  'BUY',   6, 115.00, '2023-05-15 10:00:00'),
(3, 8,  'BUY',  20,  33.00, '2023-07-20 11:00:00'),
(3, 11, 'BUY',  30,  38.00, '2023-09-05 09:30:00'),
(3, 15, 'BUY',   8, 160.00, '2023-11-01 10:00:00'),
(3, 19, 'BUY',  15,  36.00, '2024-01-15 09:00:00'),
(3, 3,  'BUY',   4, 155.00, '2024-02-20 11:00:00'),
(3, 9,  'BUY',   3, 380.00, '2024-03-10 10:30:00');

-- Price History (last 6 months sample)
INSERT INTO price_history (stock_id, price, recorded_at) VALUES
(1, 182.00, '2023-10-01'), (1, 187.50, '2023-11-01'), (1, 192.00, '2023-12-01'),
(1, 185.00, '2024-01-01'), (1, 188.00, '2024-02-01'), (1, 189.50, '2024-03-01'),
(2, 375.00, '2023-10-01'), (2, 390.00, '2023-11-01'), (2, 405.00, '2023-12-01'),
(2, 400.00, '2024-01-01'), (2, 410.00, '2024-02-01'), (2, 415.20, '2024-03-01'),
(5, 440.00, '2023-10-01'), (5, 520.00, '2023-11-01'), (5, 650.00, '2023-12-01'),
(5, 720.00, '2024-01-01'), (5, 800.00, '2024-02-01'), (5, 875.60, '2024-03-01');
