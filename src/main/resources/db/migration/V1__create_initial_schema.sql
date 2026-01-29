CREATE TABLE tb_users(
    id         UUID PRIMARY KEY,
    firstname  VARCHAR(50)  NOT NULL,
    lastname   VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE tb_bank_accounts(
    id              UUID PRIMARY KEY,
    user_id         UUID           NOT NULL,
    account_name    VARCHAR(50)    NOT NULL,
    account_type    VARCHAR(30)    NOT NULL,
    current_balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP      NOT NULL,

    CONSTRAINT fk_bank_account_user
        FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE
);

CREATE TABLE tb_credit_cards(
    id          UUID PRIMARY KEY,
    user_id     UUID           NOT NULL,
    account_id  UUID           NOT NULL,
    card_name   VARCHAR(50)    NOT NULL,
    brand       VARCHAR(30)    NOT NULL,
    card_limit  DECIMAL(10, 2) NOT NULL,
    used_limit  DECIMAL(10,2)  NOT NULL,
    closing_day INTEGER        NOT NULL,
    due_day     INTEGER        NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    updated_at  TIMESTAMP,

    CONSTRAINT fk_credit_card_user
        FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE,

    CONSTRAINT fk_credit_card_account
        FOREIGN KEY (account_id) REFERENCES tb_bank_accounts (id)
);

CREATE TABLE tb_categories(
    id          UUID PRIMARY KEY,
    user_id     UUID        NOT NULL,
    name        VARCHAR(50) NOT NULL,
    total_spent DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_category_user
        FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE
);

CREATE TABLE tb_budgets(
    id               UUID PRIMARY KEY,
    user_id          UUID           NOT NULL,
    category_id      UUID           NOT NULL,
    amount_limit     DECIMAL(12, 2) NOT NULL,
    reference_month  DATE           NOT NULL,
    alert_percentage INTEGER        NOT NULL DEFAULT 80,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL,

    CONSTRAINT fk_budget_user
        FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE,

    CONSTRAINT fk_budget_category
        FOREIGN KEY (category_id) REFERENCES tb_categories (id) ON DELETE CASCADE
);

CREATE TABLE tb_invoices(
    id              UUID PRIMARY KEY,
    card_id         UUID           NOT NULL,
    due_date        DATE           NOT NULL,
    closing_date    DATE           NOT NULL,
    reference_month DATE           NOT NULL,
    total_amount    DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    amount_paid     DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(10)    NOT NULL,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP,

    CONSTRAINT fk_invoice_card
        FOREIGN KEY (card_id) REFERENCES tb_credit_cards (id)
);

CREATE TABLE tb_transactions(
    id                 UUID PRIMARY KEY,
    category_id        UUID           NOT NULL,
    value              DECIMAL(10, 2) NOT NULL,
    transaction_status VARCHAR(20)    NOT NULL,
    description        VARCHAR(255),
    transaction_date   TIMESTAMP      NOT NULL,

    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id) REFERENCES tb_categories (id)
);

CREATE TABLE tb_bank_transactions(
    transaction_id   UUID PRIMARY KEY,
    account_id       UUID        NOT NULL,
    operation        VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,

    CONSTRAINT fk_bank_transaction_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES tb_transactions (id) ON DELETE CASCADE,

    CONSTRAINT fk_bank_transaction_account
        FOREIGN KEY (account_id)
            REFERENCES tb_bank_accounts (id)
);

CREATE TABLE tb_card_transactions(
    transaction_id     UUID PRIMARY KEY,
    card_id            UUID        NOT NULL,
    invoice_id         UUID,
    installment_number INTEGER     NOT NULL DEFAULT 1,
    total_installments INTEGER     NOT NULL DEFAULT 1,

    CONSTRAINT fk_card_transaction_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES tb_transactions (id) ON DELETE CASCADE,

    CONSTRAINT fk_card_transaction_card
        FOREIGN KEY (card_id)
            REFERENCES tb_credit_cards (id),

    CONSTRAINT fk_card_transaction_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES tb_invoices (id)
);

CREATE TABLE tb_monthly_summaries(
    id              UUID PRIMARY KEY,
    bank_account_id UUID           NOT NULL,
    reference_month DATE           NOT NULL,
    total_expense   DECIMAL(10, 2) NOT NULL,
    total_revenue   DECIMAL(10, 2) NOT NULL,
    final_balance   DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_monthly_summary_account
        FOREIGN KEY (bank_account_id) REFERENCES tb_bank_accounts (id) ON DELETE CASCADE
);
