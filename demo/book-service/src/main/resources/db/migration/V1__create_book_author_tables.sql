CREATE TABLE authors
(
    id        BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL
);
CREATE TABLE books
(
    id        BIGSERIAL PRIMARY KEY,
    author_id BIGINT,
    title     TEXT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE tags
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE tag_book
(
    tag_id  BIGINT,
    book_id BIGINT,
    PRIMARY KEY (tag_id, book_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
)