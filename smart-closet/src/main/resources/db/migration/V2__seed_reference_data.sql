
INSERT INTO roles (name) VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN');

INSERT INTO categories (name) VALUES
    ('Верх'),
    ('Низ'),
    ('Обувь'),
    ('Верхняя одежда'),
    ('Аксессуары'),
    ('Платья');

INSERT INTO brands (name) VALUES
    ('Zara'),
    ('H&M'),
    ('Uniqlo'),
    ('Massimo Dutti'),
    ('Mango');

INSERT INTO tags (name) VALUES
    ('casual'),
    ('formal'),
    ('sport'),
    ('vintage'),
    ('streetwear');

INSERT INTO users (username, email, password) VALUES
    ('elvina_admin', 'admin@smartcloset.local',
     '$2a$10$51ReTalxTGcha4mJi9mHBuVL4aJxPKCY8tgOdOMkRFRS8hcrQGDju');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'elvina_admin'
  AND r.name IN ('ROLE_USER', 'ROLE_ADMIN');
