ALTER TABLE tb_transactions
    ADD COLUMN IF NOT EXISTS account_id UUID NOT NULL;

ALTER TABLE tb_transactions
    ADD CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)
            REFERENCES tb_bank_accounts (id);

ALTER TABLE tb_bank_transactions
    DROP CONSTRAINT IF EXISTS fk_bank_transaction_account;

ALTER TABLE tb_bank_transactions
    DROP COLUMN IF EXISTS account_id;