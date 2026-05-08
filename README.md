# Teamplanner

Team- und Aufgabenverwaltung – Spring Boot + Thymeleaf + Bootstrap 5 + H2

## Voraussetzungen

| Tool | Version |
|------|---------|
| Java JDK | 17+ |
| Maven | 3.8+ |

## Schnellstart

```bash
# Repository klonen oder entpacken, dann:
mvn spring-boot:run

# Mit Dev-Profil (H2-Konsole aktiviert):
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Anwendung läuft unter: **http://localhost:8080**

## Funktionen

| Bereich | URL | Beschreibung |
|---------|-----|--------------|
| Dashboard | `/` | Kennzahlen, offene Aufgaben, Aktivitätsfeed |
| Teams | `/teams` | Teams anlegen und verwalten |
| Mitarbeiter | `/mitarbeiter` | Mitarbeiter mit Team-Zuordnung |
| Projekte | `/projekte` | Projekte mit Fortschrittsanzeige |
| Aufgaben | `/aufgaben` | Aufgabenverwaltung mit Filtern + CSV-Export |
| Todos | `/todos` | Persönliche Aufgabenliste mit Prioritäten |

## Datenbank

Die H2-Datenbank wird als Datei unter `./data/teamplanner.mv.db` gespeichert.

**H2-Konsole** (nur mit Dev-Profil erreichbar):
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/teamplanner`
- Benutzer: `sa`, Passwort: *(leer)*

## Datenbankmigrationen (Flyway)

Migrationsskripte liegen in `src/main/resources/db/migration/`.

> **Wichtig:** Bestehende Migrationsdateien niemals nachträglich ändern.
> Erweiterungen immer als neue Datei anlegen: `V3__beschreibung.sql`

## Technologie-Stack

- **Spring Boot 3.3** – Web, Data JPA, Validation
- **Thymeleaf** + Layout Dialect – Server-seitige Templates
- **Bootstrap 5.3** + Bootstrap Icons – UI-Framework
- **H2** – Dateibasierte SQL-Datenbank
- **Flyway** – Datenbankmigration
- **Lombok** – Boilerplate-Reduktion

## Projektstruktur

```
src/main/java/de/teamplanner/
├── advice/          GlobalExceptionHandler, NavbarModelAdvice
├── config/          WebConfig (Entity-Formatter)
├── controller/      Dashboard-, Team-, Mitarbeiter-, Projekt-, Aufgabe-, TodoController
├── dto/             FilterDTOs (AufgabeFilterDTO etc.)
├── exception/       EntityNotFoundException
├── model/
│   ├── enums/       Prioritaet, AufgabenStatus, ProjektStatus, TodoPrioritaet
│   └── ...          Team, Mitarbeiter, Projekt, Aufgabe, Todo
├── repository/      JpaRepository + JpaSpecificationExecutor je Entity
├── service/         Business-Logik je Entity
└── specification/   JPA Criteria API Filter je Entity
```

## Aufgaben-CSV-Export

Gefilterte Aufgaben als CSV herunterladen:

```
GET /aufgaben/export?teamId=1&status=OFFEN
```

Der Export respektiert alle aktiven Filterparameter.
