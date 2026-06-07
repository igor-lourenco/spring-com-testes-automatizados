INSERT INTO tb_user (first_name, last_name, email, password) VALUES ('Alex', 'Brown', 'alex@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');
INSERT INTO tb_user (first_name, last_name, email, password) VALUES ('Maria', 'Green', 'maria@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');

INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_category (name, created_at) VALUES ('Home appliances', now());     -- Eletrodomésticos
INSERT INTO tb_category (name, created_at) VALUES ('Books', now());               -- Livros
INSERT INTO tb_category (name, created_at) VALUES ('Sports', now());              -- Esportes
INSERT INTO tb_category (name, created_at) VALUES ('Soccer', now());              -- Futebol
INSERT INTO tb_category (name, created_at) VALUES ('Swimming', now());            -- Natação
INSERT INTO tb_category (name, created_at) VALUES ('Notebooks', now());           -- Notebooks
INSERT INTO tb_category (name, created_at) VALUES ('Smartphones', now());         -- Smartphones
INSERT INTO tb_category (name, created_at) VALUES ('Electronics', now());         -- Eletroeletrônico
INSERT INTO tb_category (name, created_at) VALUES ('Bed, table and bath', now()); -- Cama, mesa e banho
INSERT INTO tb_category (name, created_at) VALUES ('Computers', now());           -- Computadores

-- Home appliances (1)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Microwave', '800W microwave oven', 450.00, 'https://example.com/images/products/microwave.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Refrigerator', 'Frost free fridge', 3200.00, 'https://example.com/images/products/refrigerator.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Blender', 'High power blender', 200.00, 'https://example.com/images/products/blender.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Washing Machine', '11kg washer', 1800.00, 'https://example.com/images/products/washing-machine.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Air Fryer', 'Oil-free fryer', 350.00, 'https://example.com/images/products/air-fryer.png', now());

-- Books (2)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Clean Code', 'Software craftsmanship book', 120.00, 'https://example.com/images/products/clean-code.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Design Patterns', 'Classic patterns book', 150.00, 'https://example.com/images/products/design-patterns.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('DDD', 'Domain Driven Design', 180.00, 'https://example.com/images/products/domain-driven-design.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Refactoring', 'Improving code structure', 130.00, 'https://example.com/images/products/refactoring.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Effective Java', 'Java best practices', 140.00, 'https://example.com/images/products/effective-java.png', now());

-- Sports (3)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Running Shoes', 'Comfortable running shoes', 300.00, 'https://example.com/images/products/running-shoes.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Dumbbell Set', 'Adjustable weights', 250.00, 'https://example.com/images/products/dumbbell-set.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Yoga Mat', 'Non slip mat', 80.00, 'https://example.com/images/products/yoga-mat.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Tennis Racket', 'Professional racket', 500.00, 'https://example.com/images/products/tennis-racket.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Gym Gloves', 'Workout gloves', 60.00, 'https://example.com/images/products/gym-gloves.png', now());

-- Soccer (4)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Soccer Ball', 'Official size ball', 120.00, 'https://example.com/images/products/soccer-ball.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Cleats', 'Professional cleats', 350.00, 'https://example.com/images/products/cleats.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Shin Guards', 'Protection gear', 70.00, 'https://example.com/images/products/shin-guards.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Soccer Jersey', 'Team jersey', 150.00, 'https://example.com/images/products/soccer-jersey.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Goal Net', 'Training goal net', 200.00, 'https://example.com/images/products/goal-net.png', now());

-- Swimming (5)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Swimsuit', 'Professional swimsuit', 180.00, 'https://example.com/images/products/swimsuit.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Goggles', 'Anti-fog goggles', 90.00, 'https://example.com/images/products/goggles.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Swim Cap', 'Silicone cap', 40.00, 'https://example.com/images/products/swim-cap.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Fin', 'Swimming fins', 110.00, 'https://example.com/images/products/swim-fins.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Snorkel', 'Swimming snorkel', 130.00, 'https://example.com/images/products/snorkel.png', now());

-- Notebooks (6)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Dell XPS', '13 inch notebook', 8500.00, 'https://example.com/images/products/dell-xps.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('MacBook Pro', 'Apple laptop', 12000.00, 'https://example.com/images/products/macbook-pro.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Lenovo ThinkPad', 'Business laptop', 7000.00, 'https://example.com/images/products/lenovo-thinkpad.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Acer Aspire', 'Entry notebook', 3000.00, 'https://example.com/images/products/acer-aspire.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('HP Pavilion', 'Mid-range laptop', 4500.00, 'https://example.com/images/products/hp-pavilion.png', now());

-- Smartphones (7)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('iPhone 14', 'Apple smartphone', 9000.00, 'https://example.com/images/products/iphone-14.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Galaxy S23', 'Samsung flagship', 8000.00, 'https://example.com/images/products/galaxy-s23.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Xiaomi 13', 'High performance phone', 5000.00, 'https://example.com/images/products/xiaomi-13.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Moto G', 'Affordable phone', 1800.00, 'https://example.com/images/products/moto-g.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Pixel 7', 'Google smartphone', 6500.00, 'https://example.com/images/products/pixel-7.png', now());

-- Electronics (8)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Smart TV', '55 inch 4K TV', 3500.00, 'https://example.com/images/products/smart-tv.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Soundbar', 'Home theater audio', 1200.00, 'https://example.com/images/products/soundbar.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Headphones', 'Noise cancelling', 900.00, 'https://example.com/images/products/headphones.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Gaming Console', 'Next gen console', 4500.00, 'https://example.com/images/products/gaming-console.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Camera', 'Digital camera', 2500.00, 'https://example.com/images/products/camera.png', now());

-- Bed, table and bath (9)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Bed Sheet', 'Queen size sheet', 150.00, 'https://example.com/images/products/bed-sheet.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Towel', 'Cotton towel', 50.00, 'https://example.com/images/products/towel.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Pillow', 'Memory foam pillow', 120.00, 'https://example.com/images/products/pillow.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Tablecloth', 'Decorative cloth', 90.00, 'https://example.com/images/products/tablecloth.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Blanket', 'Winter blanket', 200.00, 'https://example.com/images/products/blanket.png', now());

-- Computers (10)
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Desktop PC', 'Gaming desktop', 6000.00, 'https://example.com/images/products/desktop-pc.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Mechanical Keyboard', 'RGB keyboard', 350.00, 'https://example.com/images/products/mechanical-keyboard.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Mouse', 'Wireless mouse', 120.00, 'https://example.com/images/products/mouse.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('Monitor', '27 inch display', 1400.00, 'https://example.com/images/products/monitor.png', now());
INSERT INTO tb_product (name, description, price, img_url, created_at) VALUES ('SSD 1TB', 'Fast storage', 600.00, 'https://example.com/images/products/ssd-1tb.png', now());


-- =======================================================
-- vínculo principal (cada produto com sua categoria base)


-- Home appliances
INSERT INTO tb_product_category (product_id, category_id) VALUES (1,1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (2,1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (3,1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (4,1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (5,1);

-- Books
INSERT INTO tb_product_category (product_id, category_id) VALUES (6,2);
INSERT INTO tb_product_category (product_id, category_id) VALUES (7,2);
INSERT INTO tb_product_category (product_id, category_id) VALUES (8,2);
INSERT INTO tb_product_category (product_id, category_id) VALUES (9,2);
INSERT INTO tb_product_category (product_id, category_id) VALUES (10,2);

-- Sports
INSERT INTO tb_product_category (product_id, category_id) VALUES (11,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (12,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (13,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (14,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (15,3);

-- Soccer
INSERT INTO tb_product_category (product_id, category_id) VALUES (16,4);
INSERT INTO tb_product_category (product_id, category_id) VALUES (17,4);
INSERT INTO tb_product_category (product_id, category_id) VALUES (18,4);
INSERT INTO tb_product_category (product_id, category_id) VALUES (19,4);
INSERT INTO tb_product_category (product_id, category_id) VALUES (20,4);

-- Swimming
INSERT INTO tb_product_category (product_id, category_id) VALUES (21,5);
INSERT INTO tb_product_category (product_id, category_id) VALUES (22,5);
INSERT INTO tb_product_category (product_id, category_id) VALUES (23,5);
INSERT INTO tb_product_category (product_id, category_id) VALUES (24,5);
INSERT INTO tb_product_category (product_id, category_id) VALUES (25,5);

-- Notebooks
INSERT INTO tb_product_category (product_id, category_id) VALUES (26,6);
INSERT INTO tb_product_category (product_id, category_id) VALUES (27,6);
INSERT INTO tb_product_category (product_id, category_id) VALUES (28,6);
INSERT INTO tb_product_category (product_id, category_id) VALUES (29,6);
INSERT INTO tb_product_category (product_id, category_id) VALUES (30,6);

-- Smartphones
INSERT INTO tb_product_category (product_id, category_id) VALUES (31,7);
INSERT INTO tb_product_category (product_id, category_id) VALUES (32,7);
INSERT INTO tb_product_category (product_id, category_id) VALUES (33,7);
INSERT INTO tb_product_category (product_id, category_id) VALUES (34,7);
INSERT INTO tb_product_category (product_id, category_id) VALUES (35,7);

-- Electronics
INSERT INTO tb_product_category (product_id, category_id) VALUES (36,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (37,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (38,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (39,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (40,8);

-- Bed/table/bath
INSERT INTO tb_product_category (product_id, category_id) VALUES (41,9);
INSERT INTO tb_product_category (product_id, category_id) VALUES (42,9);
INSERT INTO tb_product_category (product_id, category_id) VALUES (43,9);
INSERT INTO tb_product_category (product_id, category_id) VALUES (44,9);
INSERT INTO tb_product_category (product_id, category_id) VALUES (45,9);

-- Computers
INSERT INTO tb_product_category (product_id, category_id) VALUES (46,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (47,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (48,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (49,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (50,10);


-- Electronics + Computers
INSERT INTO tb_product_category (product_id, category_id) VALUES (46,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (47,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (48,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (49,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (50,8);

-- Notebooks também é um Computers
INSERT INTO tb_product_category (product_id, category_id) VALUES (26,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (27,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (28,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (29,10);
INSERT INTO tb_product_category (product_id, category_id) VALUES (30,10);

-- Smartphones também é um Electronics
INSERT INTO tb_product_category (product_id, category_id) VALUES (31,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (32,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (33,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (34,8);
INSERT INTO tb_product_category (product_id, category_id) VALUES (35,8);

-- Soccer também é um Sports
INSERT INTO tb_product_category (product_id, category_id) VALUES (16,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (17,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (18,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (19,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (20,3);

-- Swimming também é um Sports
INSERT INTO tb_product_category (product_id, category_id) VALUES (21,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (22,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (23,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (24,3);
INSERT INTO tb_product_category (product_id, category_id) VALUES (25,3);
