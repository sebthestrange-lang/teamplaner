# Implementierungsplan – Team & Aufgabenverwaltung

## Kontext
Flyway ist vollständig in der Progressdatei verankert. Dieser Plan beschreibt die
konkrete Umsetzungsreihenfolge, beginnend mit der Projektbasis.

---

## Was wurde zuletzt festgelegt

### Flyway-Abschnitt (Phase 2) ergänzt:
- Dependency `flyway-core` im `pom.xml`
- Migrationsordner `db/migration/` mit klarer Namenskonvention (`V1__init_schema.sql`)
- Zwei Startskripte: Schema-Init + optionale Testdaten nur im Dev-Profil
- Alle relevanten `application.properties`-Einträge dokumentiert
- **Regel verankert:** Bestehende Migrationsskripte niemals nachträglich ändern – immer neue Version anlegen

### `ddl-auto` angepasst:
- Von `update` auf `validate` geändert
- Flyway ist jetzt der **einzige Schema-Manager**
- Hibernate prüft nur noch, ob das Schema zur Entity passt

---

## Nächste Schritte (priorisiert)

### Schritt 1 – `pom.xml` erstellen
**Datei:** `pom.xml`

Enthält folgende Dependencies:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-thymeleaf`
- `thymeleaf-layout-dialect`
- `spring-boot-starter-validation`
- `h2` (scope: runtime)
- `flyway-core`
- `lombok`
- `spring-boot-devtools` (scope: runtime, optional)
- `spring-boot-starter-test` (scope: test)

---

### Schritt 2 – `application.properties` konfigurieren
**Datei:** `src/main/resources/application.properties`

```properties
# Server
server.port=8080

# H2 Datenbank (dateibasiert)
spring.datasource.url=jdbc:h2:file:./data/teamplanner
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA – Hibernate nur validieren, Flyway verwaltet das Schema
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (nur Dev)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Thymeleaf
spring.thymeleaf.cache=false
```

---

### Schritt 3 – Flyway Migrationsskripte anlegen

**Datei:** `src/main/resources/db/migration/V1__init_schema.sql`

Tabellen anlegen für:
- `teams` (id, name, farbe, erstellt_am)
- `mitarbeiter` (id, team_id FK, vorname, nachname, rolle, email, telefon, erstellt_am)
- `projekte` (id, team_id FK, name, beschreibung, status, farbe, faellig_am, erstellt_am)
- `aufgaben` (id, projekt_id FK, mitarbeiter_id FK, titel, beschreibung, prioritaet, status, faellig_am, erstellt_am, abgeschlossen_am)
- `todos` (id, titel, beschreibung, prioritaet, erledigt, faellig_am, erstellt_am, erledigt_am)

**Datei:** `src/main/resources/db/migration/V2__init_data.sql`

Optionale Testdaten (Teams, Mitarbeiter, Projekte, Aufgaben) – nur für Entwicklung.
Wird über Spring-Profil gesteuert oder manuell aktiviert.

**Regel (verbindlich):**
> Bestehende `.sql`-Dateien in `db/migration/` werden **niemals** geändert.
> Korrekturen oder Erweiterungen immer als neue Version: `V3__...`, `V4__...`

---

### Schritt 4 – Enums & Entities anlegen

**Package:** `de.teamplanner.model`

Reihenfolge:
1. Enums: `Prioritaet`, `AufgabenStatus`, `ProjektStatus`, `TodoPrioritaet`
2. Entity `Team`
3. Entity `Mitarbeiter` (FK → Team)
4. Entity `Projekt` (FK → Team)
5. Entity `Aufgabe` (FK → Projekt, Mitarbeiter)
6. Entity `Todo`

Bean Validation direkt auf den Entities (`@NotBlank`, `@Email`, `@NotNull`).

---

### Schritt 5 – Repositories anlegen

**Package:** `de.teamplanner.repository`

- `TeamRepository extends JpaRepository<Team, Long>`
- `MitarbeiterRepository` + `findByTeam(Team team)`
- `ProjektRepository` + `findByTeam`, `findByStatus`
- `AufgabeRepository` + `findByMitarbeiter`, `findByProjekt`, `findByStatus`, `findByFaelligAmBefore`
- `TodoRepository` + alle spezifischen Abfragemethoden

---

### Schritt 6 – Services anlegen

**Package:** `de.teamplanner.service`

- `TeamService`
- `MitarbeiterService`
- `ProjektService` (inkl. `getBeteiligteMitarbeiter`, `getAufgabenStatistikJeMitarbeiter`)
- `AufgabeService` (inkl. Überfälligkeitsprüfung, Filterlogik)
- `TodoService` (inkl. `erledigtToggle`, `isUeberfaellig`, `heuteFaellig`)

Eigene Exception-Klassen:
- `EntityNotFoundException`
- `ValidationException`

Globaler `@ControllerAdvice` für Fehlerseiten.

---

### Schritt 7 – Basis-Layout & Navigation

**Dateien:**
- `src/main/resources/templates/layout.html` – Haupt-Layout mit Thymeleaf Layout Dialect
- `src/main/resources/templates/fragments/sidebar.html`
- `src/main/resources/templates/fragments/navbar.html`
- `src/main/resources/templates/fragments/alerts.html`
- `src/main/resources/templates/fragments/cards.html`
- `src/main/resources/static/css/custom.css`

Design-Vorgaben:
- Dunkle Seitenleiste (250px), heller Hauptbereich
- Primärfarbe: Indigo `#6366f1`
- Bootstrap 5.3 + Bootstrap Icons

---

### Schritt 8 – Controller & Templates je Phase

Reihenfolge entspricht den Phasen in `PROGRESS.md`:

| Phase | Controller            | Templates                                      |
|-------|-----------------------|------------------------------------------------|
| 2     | TeamController        | teams/liste, detail, formular                  |
| 2     | MitarbeiterController | mitarbeiter/liste, detail, formular            |
| 3     | ProjektController     | projekte/liste, detail, formular               |
| 4     | AufgabeController     | aufgaben/liste, detail, formular               |
| 5     | DashboardController   | dashboard                                      |
| 6     | TodoController        | todos/liste, formular                          |

---

### Schritt 9 – Filter-Implementierung

**Package:** `de.teamplanner.specification`

- `AufgabeSpecification`
- `ProjektSpecification`
- `MitarbeiterSpecification`
- `TodoSpecification`

Alle Filter als `@RequestParam` mit Defaultwerten im jeweiligen Controller.
Aktive Filter als Badges mit Einzel-Entfernen-Option in den Templates.

---

### Schritt 10 – Polish & Export (Phase 7)

- CSV-Export (`/aufgaben/export`)
- Bootstrap Modals für Löschen-Bestätigung
- Toast-Benachrichtigungen (Flash-Messages, auto-hide 3s)
- Responsives Layout verifizieren
- H2-Konsole auf Dev-Profil beschränken
- Favicon + App-Titel
- `README.md`

---

## Offene Entscheidungen (noch nicht begonnen)

| Thema                     | Status     |
|---------------------------|------------|
| Spring Security / Login   | Offen      |
| Fälligkeits-Benachrichtigungen | Offen |
| Dark Mode                 | Offen      |

---

## Änderungslog

| Datum      | Beschreibung                                                          |
|------------|-----------------------------------------------------------------------|
| 2026-05-08 | Implementierungsplan initial erstellt                                 |
| 2026-05-08 | Flyway-Abschnitt und `ddl-auto=validate` dokumentiert                 |
