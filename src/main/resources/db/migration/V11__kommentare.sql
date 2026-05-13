CREATE TABLE kommentare (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    aufgabe_id  BIGINT       NOT NULL,
    benutzer_id BIGINT       NOT NULL,
    organisation_id BIGINT   NOT NULL,
    inhalt      TEXT         NOT NULL,
    erstellt_am TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (aufgabe_id)      REFERENCES aufgaben(id)      ON DELETE CASCADE,
    FOREIGN KEY (benutzer_id)     REFERENCES benutzer(id),
    FOREIGN KEY (organisation_id) REFERENCES organisationen(id)
);
