CREATE TABLE authors
(
    id        BIGSERIAL PRIMARY KEY,
    firstname TEXT NOT NULL,
    lastname  TEXT NOT NULL
);
CREATE TABLE books
(
    id        BIGSERIAL PRIMARY KEY,
    author_id BIGINT REFERENCES authors (id),
    title     TEXT NOT NULL,
    tags      TEXT,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE tags
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE tag_book
(
    tag_id  BIGINT REFERENCES tags (id)  NOT NULL,
    book_id BIGINT REFERENCES books (id) NOT NULL,
    PRIMARY KEY (tag_id, book_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
)