CREATE TABLE tags (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    farbe           VARCHAR(7)   NOT NULL DEFAULT '#6366f1',
    organisation_id BIGINT       NOT NULL,
    FOREIGN KEY (organisation_id) REFERENCES organisationen(id)
);

CREATE TABLE aufgabe_tags (
    aufgabe_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (aufgabe_id, tag_id),
    FOREIGN KEY (aufgabe_id) REFERENCES aufgaben(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)     REFERENCES tags(id)     ON DELETE CASCADE
);
