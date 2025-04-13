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
)