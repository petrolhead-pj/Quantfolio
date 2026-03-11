-- ============================================================
--  Quantfolio — Triggers
-- ============================================================
USE quantfolio;

DELIMITER $$

-- -----------------------------------------------
-- TRIGGER: After BUY — update portfolio_holdings
-- -----------------------------------------------
DROP TRIGGER IF EXISTS after_buy_transaction$$
CREATE TRIGGER after_buy_transaction
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    IF NEW.type = 'BUY' THEN
        INSERT INTO portfolio_holdings (user_id, stock_id, quantity, avg_buy_price)
        VALUES (NEW.user_id, NEW.stock_id, NEW.quantity, NEW.price)
        ON DUPLICATE KEY UPDATE
            avg_buy_price = ((avg_buy_price * quantity) + (NEW.price * NEW.quantity))
                            / (quantity + NEW.quantity),
            quantity      = quantity + NEW.quantity;
    END IF;
END$$

-- -----------------------------------------------
-- TRIGGER: Before SELL — guard against overselling
-- -----------------------------------------------
DROP TRIGGER IF EXISTS before_sell_transaction$$
CREATE TRIGGER before_sell_transaction
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    DECLARE held INT DEFAULT 0;
    IF NEW.type = 'SELL' THEN
        SELECT COALESCE(quantity, 0)
        INTO   held
        FROM   portfolio_holdings
        WHERE  user_id = NEW.user_id AND stock_id = NEW.stock_id;

        IF held < NEW.quantity THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient shares: cannot sell more than you hold.';
        END IF;
    END IF;
END$$

-- -----------------------------------------------
-- TRIGGER: After SELL — reduce portfolio_holdings
-- -----------------------------------------------
DROP TRIGGER IF EXISTS after_sell_transaction$$
CREATE TRIGGER after_sell_transaction
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    IF NEW.type = 'SELL' THEN
        UPDATE portfolio_holdings
        SET    quantity = quantity - NEW.quantity
        WHERE  user_id  = NEW.user_id
          AND  stock_id = NEW.stock_id;

        -- Remove the row entirely if quantity drops to zero
        DELETE FROM portfolio_holdings
        WHERE  user_id   = NEW.user_id
          AND  stock_id  = NEW.stock_id
          AND  quantity  = 0;
    END IF;
END$$

-- -----------------------------------------------
-- TRIGGER: Audit log on new transactions
-- -----------------------------------------------
DROP TRIGGER IF EXISTS audit_new_transaction$$
CREATE TRIGGER audit_new_transaction
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (user_id, action, detail)
    VALUES (
        NEW.user_id,
        CONCAT(NEW.type, '_STOCK'),
        CONCAT('stock_id=', NEW.stock_id,
               ', qty=', NEW.quantity,
               ', price=', NEW.price)
    );
END$$

DELIMITER ;
