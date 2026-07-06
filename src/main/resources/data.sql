INSERT INTO books (isbn, title, author, total_copies, available_copies) VALUES
    ('978-0134685991', 'Effective Java', 'Joshua Bloch', 3, 2),
    ('978-0596007126', 'Head First Design Patterns', 'Eric Freeman', 2, 2),
    ('978-0135166307', 'Clean Code', 'Robert C. Martin', 4, 3),
    ('978-1491950357', 'Building Microservices', 'Sam Newman', 2, 1),
    ('978-0201633610', 'Design Patterns', 'Erich Gamma', 3, 3),
    ('978-0132350884', 'Clean Architecture', 'Robert C. Martin', 2, 2);

INSERT INTO members (name, email) VALUES
    ('Alice Johnson', 'alice.johnson@example.com'),
    ('Ben Carter', 'ben.carter@example.com'),
    ('Clara Nguyen', 'clara.nguyen@example.com'),
    ('David Smith', 'david.smith@example.com');

INSERT INTO loans (book_id, member_id, loaned_at, returned_at) VALUES
    (1, 1, '2026-06-01 10:00:00', NULL),
    (3, 2, '2026-06-15 09:30:00', NULL),
    (4, 3, '2026-06-20 11:00:00', NULL),
    (2, 1, '2026-05-01 10:00:00', '2026-05-15 10:00:00'),
    (5, 4, '2026-04-10 10:00:00', '2026-04-24 10:00:00');

INSERT INTO ratings (book_id, member_id, score, comment) VALUES
    (2, 1, 5, 'Loved the examples, made patterns click.'),
    (2, 4, 4, 'Solid intro to patterns.'),
    (5, 4, 5, 'The classic GoF reference.'),
    (1, 2, 4, 'Dense but incredibly useful.'),
    (3, 1, 5, NULL);
