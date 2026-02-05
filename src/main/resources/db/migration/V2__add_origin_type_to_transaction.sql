ALTER TABLE tb_transactions
ADD COLUMN transaction_origin_type VARCHAR(10) NOT NULL;

ALTER TABLE tb_transactions
ADD CONSTRAINT chk_transaction_origin_type
CHECK (transaction_origin_type IN ('BANK', 'CARD'));