ALTER TABLE tb_credit_cards
DROP CONSTRAINT IF EXISTS fk_credit_card_user;

ALTER TABLE tb_credit_cards
DROP COLUMN IF EXISTS user_id;

ALTER TABLE tb_credit_cards
DROP CONSTRAINT IF EXISTS fk_credit_card_account;

ALTER TABLE tb_credit_cards
    ADD CONSTRAINT fk_credit_card_account
        FOREIGN KEY (account_id)
            REFERENCES tb_bank_accounts (id)
            ON DELETE CASCADE;

ALTER TABLE tb_credit_cards
    ALTER COLUMN used_limit SET DEFAULT 0.00;

ALTER TABLE tb_credit_cards
    ALTER COLUMN account_id SET NOT NULL;

-- Categorias

ALTER TABLE tb_categories
    DROP CONSTRAINT IF EXISTS fk_category_user;

ALTER TABLE tb_categories
    DROP COLUMN IF EXISTS user_id;

ALTER TABLE tb_categories
    ADD COLUMN account_id UUID;

ALTER TABLE tb_categories
    ADD CONSTRAINT fk_category_account
        FOREIGN KEY (account_id)
            REFERENCES tb_bank_accounts(id)
            ON DELETE CASCADE;