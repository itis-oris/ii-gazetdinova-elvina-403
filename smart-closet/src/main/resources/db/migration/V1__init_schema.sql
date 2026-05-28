
CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE brands (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE items (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    color            VARCHAR(20),
    size             VARCHAR(20),
    season           VARCHAR(20),
    price            NUMERIC(10, 2),
    image_url        VARCHAR(500),
    purchase_date    DATE,
    color_name       VARCHAR(100),
    complement_color VARCHAR(20),
    user_id          BIGINT REFERENCES users (id)      ON DELETE CASCADE,
    category_id      BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    brand_id         BIGINT REFERENCES brands (id)     ON DELETE SET NULL
);
CREATE INDEX idx_items_user_id   ON items (user_id);
CREATE INDEX idx_items_color     ON items (color);
CREATE INDEX idx_items_season    ON items (season);

CREATE TABLE item_tags (
    item_id BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    tag_id  BIGINT NOT NULL REFERENCES tags (id)  ON DELETE CASCADE,
    PRIMARY KEY (item_id, tag_id)
);

CREATE TABLE purchases (
    id            BIGSERIAL PRIMARY KEY,
    purchase_date DATE NOT NULL,
    price         NUMERIC(10, 2),
    note          VARCHAR(500),
    user_id       BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id       BIGINT REFERENCES items (id) ON DELETE CASCADE
);
CREATE INDEX idx_purchases_user_id ON purchases (user_id);

CREATE TABLE wishlist_items (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    price       NUMERIC(10, 2),
    url         VARCHAR(500),
    note        VARCHAR(500),
    added_date  DATE,
    user_id     BIGINT REFERENCES users (id)      ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    brand_id    BIGINT REFERENCES brands (id)     ON DELETE SET NULL
);
CREATE INDEX idx_wishlist_user_id ON wishlist_items (user_id);
