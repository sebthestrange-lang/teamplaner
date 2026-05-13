CREATE TABLE aufgabe_abhaengigkeiten (
    aufgabe_id      BIGINT NOT NULL,
    blockiert_von_id BIGINT NOT NULL,
    PRIMARY KEY (aufgabe_id, blockiert_von_id),
    FOREIGN KEY (aufgabe_id)       REFERENCES aufgaben(id) ON DELETE CASCADE,
    FOREIGN KEY (blockiert_von_id) REFERENCES aufgaben(id) ON DELETE CASCADE
);
