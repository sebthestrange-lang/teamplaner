-- V1: Initiales Datenbankschema
-- WICHTIG: Diese Datei niemals nachträglich ändern – immer neue Version anlegen!

CREATE TABLE IF NOT EXISTS teams (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    farbe      VARCHAR(50)  NOT NULL DEFAULT '#6366f1',
    erstellt_am TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mitarbeiter (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id     BIGINT,
    vorname     VARCHAR(255) NOT NULL,
    nachname    VARCHAR(255) NOT NULL,
    rolle       VARCHAR(255),
    email       VARCHAR(255),
    telefon     VARCHAR(50),
    erstellt_am TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS projekte (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id      BIGINT,
    name         VARCHAR(255) NOT NULL,
    beschreibung TEXT,
    status       VARCHAR(50)  NOT NULL DEFAULT 'GEPLANT',
    farbe        VARCHAR(50)           DEFAULT '#6366f1',
    faellig_am   DATE,
    erstellt_am  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS aufgaben (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    projekt_id       BIGINT,
    mitarbeiter_id   BIGINT,
    titel            VARCHAR(255) NOT NULL,
    beschreibung     TEXT,
    prioritaet       VARCHAR(50)  NOT NULL DEFAULT 'MITTEL',
    status           VARCHAR(50)  NOT NULL DEFAULT 'OFFEN',
    faellig_am       DATE,
    erstellt_am      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    abgeschlossen_am TIMESTAMP,
    FOREIGN KEY (projekt_id)     REFERENCES projekte(id)    ON DELETE CASCADE,
    FOREIGN KEY (mitarbeiter_id) REFERENCES mitarbeiter(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS todos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    titel        VARCHAR(255) NOT NULL,
    beschreibung TEXT,
    prioritaet   VARCHAR(50)  NOT NULL DEFAULT 'MITTEL',
    erledigt     BOOLEAN      NOT NULL DEFAULT FALSE,
    faellig_am   DATE,
    erstellt_am  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    erledigt_am  TIMESTAMP
);
