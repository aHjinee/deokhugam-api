-- 한글 2-gram 검색용 pg_bigm 확장 (도서 검색 인덱스에 사용)
CREATE EXTENSION IF NOT EXISTS pg_bigm;

DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS dashboard_batch_execution CASCADE;

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE books
(
    id             UUID PRIMARY KEY,
    isbn           VARCHAR(50)      NOT NULL UNIQUE,
    title          VARCHAR(255)     NOT NULL,
    author         VARCHAR(255)     NOT NULL,
    description    TEXT             NOT NULL,
    publisher      VARCHAR(100)     NOT NULL,
    published_date DATE             NOT NULL,
    thumbnail_url  TEXT,
    review_count   INTEGER          NOT NULL DEFAULT 0,
    total_score    INTEGER          NOT NULL DEFAULT 0,
    rating         DOUBLE PRECISION NOT NULL DEFAULT 0.0
        CONSTRAINT chk_books_rating
            CHECK (rating >= 0 AND rating <= 5),
    created_at     TIMESTAMPTZ      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMPTZ
);

-- 도서 검색용 bigm GIN 인덱스 (LIKE 부분검색 + =% 유사도 가속)
CREATE INDEX idx_book_title_bigm  ON books USING gin (replace(lower(title),  ' ', '') gin_bigm_ops);
CREATE INDEX idx_book_author_bigm ON books USING gin (replace(lower(author), ' ', '') gin_bigm_ops);
CREATE INDEX idx_book_isbn_bigm   ON books USING gin (isbn gin_bigm_ops);

CREATE TABLE reviews
(
    id            UUID PRIMARY KEY,
    user_id       UUID        NOT NULL,
    book_id       UUID        NOT NULL,
    content       TEXT        NOT NULL,
    rating        INTEGER     NOT NULL
        CONSTRAINT chk_reviews_rating CHECK (rating >= 1 AND rating <= 5),
    like_count    INTEGER     NOT NULL DEFAULT 0,
    comment_count INTEGER     NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMPTZ,

    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_review_book
        FOREIGN KEY (book_id)
            REFERENCES books (id)
            ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         UUID PRIMARY KEY,
    review_id  UUID         NOT NULL,
    user_id    UUID         NOT NULL,
    content    VARCHAR(500) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_review
        FOREIGN KEY (review_id)
            REFERENCES reviews (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE review_likes
(
    id         UUID PRIMARY KEY,
    review_id  UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_like_review
        FOREIGN KEY (review_id)
            REFERENCES reviews (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_like_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_review_like
        UNIQUE (review_id, user_id)
);

CREATE TABLE notifications
(
    id             UUID PRIMARY KEY,
    user_id        UUID        NOT NULL,
    review_id      UUID        NOT NULL,
    review_content TEXT        NOT NULL,
    message        TEXT        NOT NULL,
    confirmed      BOOLEAN     NOT NULL DEFAULT FALSE,
    type           VARCHAR(50) NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_notification_review
        FOREIGN KEY (review_id)
            REFERENCES reviews (id)
            ON DELETE CASCADE
);

CREATE TABLE dashboard_batch_execution
(
    id          UUID PRIMARY KEY,
    job_name    VARCHAR(100) NOT NULL,
    period_type VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    started_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_active_review
    ON reviews (user_id, book_id) WHERE deleted_at IS NULL;
