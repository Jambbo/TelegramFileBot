CREATE TABLE IF NOT EXISTS app_user
(
    id BIGSERIAL NOT NULL,
    email VARCHAR(255),
    first_login_date TIMESTAMP NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    last_name VARCHAR(255),
    state VARCHAR(255),
    telegram_user_id BIGINT NOT NULL UNIQUE ,
    username VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
    CREATE INDEX idx_email
    ON app_user(email);

CREATE TABLE IF NOT EXISTS raw_data
(
    id BIGSERIAL NOT NULL,
    event jsonb NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS binary_content
(
    id BIGSERIAL NOT NULL,
    file_as_array_of_bytes bytea NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS app_document
(
    id BIGSERIAL NOT NULL,
    doc_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(255) NOT NULL,
    telegram_file_id VARCHAR(255) NOT NULL,
    binary_content_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_app_document_binary_content FOREIGN KEY (binary_content_id)
        REFERENCES binary_content(id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS app_photo
(
    id BIGSERIAL NOT NULL,
    file_size INT NOT NULL,
    telegram_file_id VARCHAR(255) NOT NULL,
    binary_content_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_app_photo_binary_content FOREIGN KEY (binary_content_id)
        REFERENCES binary_content(id) ON DELETE CASCADE ON UPDATE NO ACTION
)
