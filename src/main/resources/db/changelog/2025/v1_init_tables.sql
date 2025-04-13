CREATE TABLE IF NOT EXISTS user_infos (
    id              UUID          PRIMARY KEY,
    name            TEXT,
    email           TEXT,
    phone           TEXT,
    address         TEXT,
    created_date    TIMESTAMPTZ   NOT NULL,
    updated_date    TIMESTAMPTZ   NOT NULL
);

CREATE INDEX idx_user_infos_name        ON user_infos(name);
CREATE INDEX idx_user_infos_email       ON user_infos(email);
CREATE INDEX idx_user_infos_phone       ON user_infos(phone);
CREATE INDEX idx_user_infos_address     ON user_infos(address);

CREATE TABLE IF NOT EXISTS users (
    id                      UUID            PRIMARY KEY,
    email                   TEXT            NOT NULL UNIQUE,
    password                TEXT            NOT NULL,
    account_locked          BOOLEAN,
    email_verified          BOOLEAN,
    created_date            TIMESTAMPTZ     NOT NULL,
    updated_date            TIMESTAMPTZ     NOT NULL,
    is_two_factor_enabled   BOOLEAN,
    user_info_id            UUID,

    CONSTRAINT fk_user_infos_in_users FOREIGN KEY (user_info_id) REFERENCES user_infos(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS roles (
    id              UUID            PRIMARY KEY,
    name            TEXT            NOT NULL UNIQUE,
    created_date    TIMESTAMPTZ     NOT NULL,
    updated_date    TIMESTAMPTZ     NOT NULL
);

CREATE INDEX idx_roles_name ON roles(name);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id    UUID     NOT NULL,
    role_id    UUID     NOT NULL,

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_in_users_roles FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_roles_in_users_roles FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS token_types(
    id          UUID      PRIMARY KEY,
    name        TEXT      NOT NULL
);

CREATE INDEX idx_token_types_name ON roles(name);

CREATE TABLE IF NOT EXISTS tokens (
    id                UUID            PRIMARY KEY,
    token             TEXT            NOT NULL,
    created_date      TIMESTAMPTZ     NOT NULL,
    expires_date      TIMESTAMPTZ     NOT NULL,
    token_type_id     UUID            NOT NULL,
    user_id           UUID            NOT NULL,

    CONSTRAINT fk_token_types_in_tokens FOREIGN KEY (token_type_id) REFERENCES token_types(id)  ON DELETE CASCADE,
    CONSTRAINT fk_users_in_tokens       FOREIGN KEY (user_id)       REFERENCES users(id)        ON DELETE CASCADE
);

CREATE INDEX idx_tokens_token ON tokens(token);

CREATE TABLE IF NOT EXISTS login_info_changes (
    id              UUID            PRIMARY KEY,
    change_type     TEXT            NOT NULL,
    value           TEXT            NOT NULL,
    created_date    TIMESTAMPTZ     NOT NULL,
    updated_date    TIMESTAMPTZ     NOT NULL,
    user_id         UUID            NOT NULL,

    CONSTRAINT fk_users_in_login_info_changes   FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS types (
    id          UUID   PRIMARY KEY,
    value        TEXT        NOT NULL        UNIQUE,
    is_visible  BOOLEAN
);

CREATE TABLE IF NOT EXISTS megapixels (
    id          UUID   PRIMARY KEY,
    value       TEXT        NOT NULL        UNIQUE,
    is_visible  BOOLEAN
);

CREATE TABLE IF NOT EXISTS millimeters (
    id          UUID   PRIMARY KEY,
    value       TEXT        NOT NULL        UNIQUE,
    is_visible  BOOLEAN
);

CREATE TABLE IF NOT EXISTS products (
    id              UUID   PRIMARY KEY,
    unique_name     TEXT        NOT NULL        UNIQUE,
    is_visible      BOOLEAN,
    video_url       TEXT
);

CREATE INDEX idx_products_unique_name ON products(unique_name);

CREATE TABLE IF NOT EXISTS images (
    id              UUID   PRIMARY KEY,
    name            TEXT,
    mime_type       TEXT        NOT NULL
);

CREATE TABLE IF NOT EXISTS product_infos (
    id                      UUID       PRIMARY KEY,
    name                    TEXT            NOT NULL,
    update_date             TIMESTAMPTZ     NOT NULL,
    price                   NUMERIC(19, 2)  NOT NULL,
    discount                NUMERIC(19, 2)  NOT NULL,
    is_interest_discount    BOOLEAN,
    description             TEXT,
    millimeter_id           UUID          NOT NULL,
    megapixel_id            UUID          NOT NULL,
    type_id                 UUID          NOT NULL,
    product_id              UUID          NOT NULL,

    CONSTRAINT fk_millimeters_in_product_infos  FOREIGN KEY (millimeter_id)   REFERENCES millimeters(id)  ON DELETE CASCADE,
    CONSTRAINT fk_megapixels_in_product_infos   FOREIGN KEY (megapixel_id)    REFERENCES megapixels(id)   ON DELETE CASCADE,
    CONSTRAINT fk_types_in_product_infos        FOREIGN KEY (type_id)         REFERENCES types(id)        ON DELETE CASCADE,
    CONSTRAINT fk_products_in_product_infos     FOREIGN KEY (product_id)      REFERENCES products(id)     ON DELETE CASCADE
);

CREATE INDEX idx_product_infos_name         ON product_infos(name);
CREATE INDEX idx_product_infos_update_date  ON product_infos(update_date);

CREATE TABLE IF NOT EXISTS product_infos_images (
    product_info_id    UUID NOT NULL,
    image_id           UUID NOT NULL,
    number             BIGINT NOT NULL,

    PRIMARY KEY (product_info_id, image_id),
    CONSTRAINT fk_product_infos_in_product_infos FOREIGN KEY (product_info_id)   REFERENCES product_infos(id)  ON DELETE CASCADE,
    CONSTRAINT fk_images_in_product_infos        FOREIGN KEY (image_id)          REFERENCES images(id)         ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS orders (
    id              UUID       PRIMARY KEY,
    order_date      TIMESTAMPTZ     NOT NULL,
    description     TEXT,
    user_info_id    UUID          NOT NULL,
    user_id         UUID,

    CONSTRAINT fk_user_info_in_orders FOREIGN KEY (user_info_id) REFERENCES user_infos(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_in_orders FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS orders_product_infos (
    order_id            UUID NOT NULL,
    product_info_id     UUID NOT NULL,
    quantity            BIGINT  NOT NULL,

    PRIMARY KEY (order_id, product_info_id),
    CONSTRAINT fk_order_in_orders_product_infos         FOREIGN KEY (order_id)        REFERENCES orders(id)         ON DELETE CASCADE,
    CONSTRAINT fk_product_info_in_orders_product_infos  FOREIGN KEY (product_info_id) REFERENCES product_infos(id)  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS cart (
    user_id         UUID NOT NULL,
    product_id      UUID NOT NULL,
    quantity        BIGINT  NOT NULL,

    PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_user_in_cart     FOREIGN KEY (user_id)        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_in_cart  FOREIGN KEY (product_id)     REFERENCES products(id) ON DELETE CASCADE
    );
