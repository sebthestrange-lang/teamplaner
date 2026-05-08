# PROGRESS – Team & Aufgabenverwaltung
Spring Boot + Thymeleaf + Bootstrap 5 + H2 (dateibasiert)

## Status-Legende
- [ ] Offen
- [~] In Bearbeitung
- [x] Abgeschlossen

---

## Phase 1 – Projektsetup
- [x] Maven-Projekt anlegen (Spring Initializr)
- [x] Dependencies konfigurieren (pom.xml)
  - Spring Boot Web
  - Spring Data JPA
  - Thymeleaf
  - H2 Database
  - Spring Boot Validation
  - Lombok
- [x] application.properties konfigurieren (H2 dateibasiert, Port 8080)
- [x] Basis-Layout (layout.html) mit Thymeleaf Layout Dialect
- [x] Navigation (Seitenleiste) mit Bootstrap 5
- [x] Startseite erreichbar unter localhost:8080

---

## Phase 2 – Team & Mitarbeiter

### Entities & Enums
- [x] Enum `Prioritaet` (NIEDRIG, MITTEL, HOCH, KRITISCH)
- [x] Enum `AufgabenStatus` (OFFEN, IN_BEARBEITUNG, ABGESCHLOSSEN)
- [x] Enum `ProjektStatus` (GEPLANT, AKTIV, ABGESCHLOSSEN)
- [x] Entity `Team` (id, name, farbe, erstelltAm)
- [x] Entity `Mitarbeiter` (id, team, vorname, nachname, rolle, email, telefon, erstelltAm)

### Repository
- [x] `TeamRepository` (JpaRepository)
- [x] `MitarbeiterRepository` (JpaRepository + findByTeam)

### Service
- [x] `TeamService` (CRUD)
- [x] `MitarbeiterService` (CRUD, findByTeam)

### Controller & Templates
- [x] `TeamController` (GET /teams, GET /teams/{id}, POST, PUT, DELETE)
- [x] Template `teams/liste.html`
- [x] Template `teams/detail.html`
- [x] Template `teams/formular.html`
- [x] `MitarbeiterController` (GET /mitarbeiter, GET /{id}, POST, PUT, DELETE)
- [x] Template `mitarbeiter/liste.html`
- [x] Template `mitarbeiter/detail.html`
- [x] Template `mitarbeiter/formular.html`

### Flyway Datenbankmigration
- [x] Flyway-Dependency in pom.xml (`org.flywaydb:flyway-core`)
- [x] Migrationsordner anlegen: `src/main/resources/db/migration/`
- [x] Namenskonvention: `V{Version}__{Beschreibung}.sql` (z. B. `V1__init_schema.sql`)
- [x] Migrationsskripte
  - [x] `V1__init_schema.sql` – Tabellen anlegen (teams, mitarbeiter, projekte, aufgaben, todos)
  - [x] `V2__init_data.sql` – optionale Testdaten (nur Dev-Profil)
- [x] application.properties Flyway-Konfiguration
  - `spring.flyway.enabled=true`
  - `spring.flyway.locations=classpath:db/migration`
  - `spring.flyway.baseline-on-migrate=true`
  - `spring.jpa.hibernate.ddl-auto=validate`
- [x] **Regel:** Bestehende Migrationsskripte niemals nachträglich ändern – immer neue Version anlegen
- [x] Neue Features / Spalten immer als neue Migrationsdatei (z. B. `V3__add_todo_table.sql`)
- [x] Bean Validation (`@NotBlank`, `@Email`, etc.) auf Entities
- [x] Fehlermeldungen in Templates anzeigen

---

## Phase 3 – Projekte

### Entity & Repository
- [x] Entity `Projekt` (id, team, name, beschreibung, status, farbe, faelligAm, erstelltAm)
- [x] `ProjektRepository` (findByTeam, findByStatus)

### Service & Controller
- [x] `ProjektService` (CRUD, findByTeam)
- [x] `ProjektController` (GET /projekte, GET /{id}, POST, PUT, DELETE)

### Templates
- [x] Template `projekte/liste.html` (Filterbar nach Team)
- [x] Template `projekte/detail.html`
  - [x] Aufgabenliste mit Fortschrittsbalken (erledigte / gesamt)
  - [x] Beteiligte Mitarbeiter-Übersicht
    - [x] Avatar-Kacheln mit Name, Rolle und Team
    - [x] Anzahl offener Aufgaben je Mitarbeiter im Projekt
    - [x] Anzahl erledigter Aufgaben je Mitarbeiter im Projekt
    - [x] Mitarbeiter-Kachel verlinkt auf Mitarbeiter-Detailansicht
    - [x] Mitarbeiter werden automatisch aus den zugewiesenen Aufgaben des Projekts ermittelt (kein manuelles Zuweisen nötig)
- [x] Template `projekte/formular.html`

### Service-Erweiterung
- [x] `ProjektService.getBeteiligteMitarbeiter(projektId)` – liefert distinct Mitarbeiter anhand der Aufgaben des Projekts
- [x] `ProjektService.getAufgabenStatistikJeMitarbeiter(projektId)` – Map mit offenen / erledigten Aufgaben je Mitarbeiter

---

## Phase 4 – Aufgaben

### Entity & Repository
- [x] Entity `Aufgabe` (id, projekt, mitarbeiter, titel, beschreibung, prioritaet, status, faelligAm, erstelltAm, abgeschlossenAm)
- [x] `AufgabeRepository` (findByMitarbeiter, findByProjekt, findByStatus, findByFaelligAmBefore)

### Service & Controller
- [x] `AufgabeService` (CRUD, Filterlogik, Überfälligkeitsprüfung)
- [x] `AufgabeController`
  - GET /aufgaben (Filter: teamId, mitarbeiterId, projektId, status)
  - GET /aufgaben/{id}
  - POST /aufgaben
  - PUT /aufgaben/{id}
  - PATCH /aufgaben/{id}/status
  - DELETE /aufgaben/{id}

### Templates
- [x] Template `aufgaben/liste.html`
  - [x] Filterleiste (Team, Mitarbeiter, Projekt, Status, Priorität)
  - [x] Überfällige Aufgaben rot hervorheben
- [x] Template `aufgaben/detail.html`
- [x] Template `aufgaben/formular.html`

---

## Phase 5 – Dashboard
- [x] `DashboardController` (GET /)
- [x] Kennzahlen-Widgets
  - [x] Anzahl Teams
  - [x] Anzahl Mitarbeiter
  - [x] Offene Aufgaben gesamt
  - [x] Überfällige Aufgaben
- [x] Aufgaben fällig heute (Tabelle)
- [x] Aktivitätsfeed (zuletzt geänderte Aufgaben)
- [x] Auslastungsübersicht je Mitarbeiter (offene Aufgaben)
- [x] Template `dashboard.html`

---

## Phase 6 – Todo-Modul

### Enum & Entity
- [x] Enum `TodoPrioritaet` (NIEDRIG, MITTEL, HOCH, KRITISCH) – mit zugeordneter Farbe
  - NIEDRIG → `#10b981` (Grün)
  - MITTEL → `#f59e0b` (Gelb)
  - HOCH → `#f97316` (Orange)
  - KRITISCH → `#ef4444` (Rot)
- [x] Entity `Todo`
  - id (Long)
  - titel (String, @NotBlank)
  - beschreibung (String, optional)
  - prioritaet (TodoPrioritaet)
  - erledigt (boolean, default false)
  - faelligAm (LocalDate)
  - erstelltAm (LocalDateTime, auto)
  - erledigtAm (LocalDateTime, nullable)

### Repository & Service
- [x] `TodoRepository`
  - `findByErledigtFalseOrderByFaelligAmAsc()`
  - `findByErledigtTrue()`
  - `findByFaelligAmBeforeAndErledigtFalse(LocalDate heute)` – überfällige Todos
  - `findByFaelligAmAndErledigtFalse(LocalDate heute)` – heute fällige Todos
  - `findByPrioritaet(TodoPrioritaet)`
- [x] `TodoService`
  - `alle()` / `alleOffen()` / `alleErledigt()`
  - `anlegen(Todo)` / `aktualisieren(Todo)` / `loeschen(id)`
  - `erledigtToggle(id)` – setzt erledigt + erledigtAm
  - `isUeberfaellig(Todo)` – Hilfsmethode
  - `heuteFaellig()` – für Dashboard-Widget

### Controller
- [x] `TodoController`
  - GET /todos – Liste (Filter: prioritaet, status, ueberfaellig)
  - GET /todos/neu – Formular anzeigen
  - POST /todos – Todo anlegen
  - GET /todos/{id}/bearbeiten – Formular mit Daten
  - POST /todos/{id}/bearbeiten – Todo speichern
  - POST /todos/{id}/erledigt – Toggle erledigt (per Button/Checkbox)
  - POST /todos/{id}/loeschen – Todo löschen

### Templates
- [x] Template `todos/liste.html`
  - [x] Schnell-Anlegen-Formular oben auf der Seite (Titel, Priorität, Fälligkeitsdatum)
  - [x] Karten-Layout (Card-Grid) mit Farbkennzeichnung
    - [x] Linker farbiger Rand je Priorität (CSS `border-left`)
    - [x] Farbiger Badge mit Prioritätsbezeichnung
    - [x] Fälligkeitsdatum gut sichtbar angezeigt
  - [x] Farbliche Hervorhebung nach Zustand
    - Überfällig + offen → roter Hintergrund (`#fef2f2`)
    - Heute fällig → gelber Hintergrund (`#fefce8`)
    - Erledigt → ausgegraut, Titel durchgestrichen, Karte transparent
  - [x] Filterleiste
    - Filter: Alle / Offen / Erledigt / Überfällig / Heute fällig
    - Filter nach Priorität (Dropdown)
  - [x] Erledigungs-Checkbox / Button direkt auf der Karte
  - [x] Bearbeiten- und Löschen-Button je Karte
- [x] Template `todos/formular.html`
  - [x] Felder: Titel, Beschreibung, Priorität, Fälligkeitsdatum
  - [x] Validierungsfehlermeldungen

### Navigation & Dashboard
- [x] Menüpunkt „Todos ✅" in der Seitenleiste ergänzen
- [x] Badge im Menü mit Anzahl offener Todos
- [x] Dashboard-Widget „Todos"
  - [x] Anzahl offener Todos gesamt
  - [x] Anzahl heute fälliger Todos
  - [x] Anzahl überfälliger Todos (rot hervorgehoben)
  - [x] Direktlink zur Todo-Liste

---

## Phase 7 – Polish & Export
- [x] CSV-Export für Aufgaben (`/aufgaben/export`)
- [x] Bestätigungsdialoge bei Löschen (Bootstrap Modal)
- [x] Erfolgsmeldungen / Flash-Messages nach Aktionen
- [x] Responsives Layout prüfen
- [x] H2-Konsole absichern (nur Dev-Profil)
- [x] Favicon & App-Titel setzen
- [x] README.md erstellen (Setup-Anleitung)

---

## Phase 8 – Team-Erweiterungen

### Mitarbeiter-Zuweisung direkt im Team
- [x] Mitarbeiter aus Team-Detailseite heraus zuweisen (`POST /teams/{id}/mitarbeiter/zuweisen`)
- [x] Mitarbeiter aus Team-Detailseite entfernen (`POST /teams/{id}/mitarbeiter/{mid}/entfernen`)
- [x] Dropdown zeigt alle nicht zugewiesenen + aktuell anderem Team zugewiesenen Mitarbeiter
- [x] Repository: `findByTeamIsNullOrTeamNotOrderByNachnameAsc(Team)`
- [x] Bestätigungsdialog beim Entfernen (`confirm()`)

### Team-Auslastung
- [x] `TeamAuslastungDTO` (offen, inBearbeitung, abgeschlossen, aktiv(), gesamt(), auslastungProzent(), bootstrapFarbe())
- [x] Repository-Query `countByTeamAndStatus` (JPQL über `mitarbeiter.team`)
- [x] `AufgabeService.auslastungFuerTeam(Team)` berechnet Auslastung je Team
- [x] `TeamController.liste()` liefert `Map<Long, TeamAuslastungDTO>` → `auslastungen`
- [x] `TeamController.detail()` liefert `TeamAuslastungDTO` → `auslastung`
- [x] Team-Liste (`/teams`): Fortschrittsbalken + "X / Y Aufgaben aktiv" je Team-Karte
  - Grün (< 50 % aktiv), Gelb (50–79 %), Rot (≥ 80 %)
- [x] Team-Detail (`/teams/{id}`): 4 Stat-Kacheln (Offen, In Bearbeitung, Abgeschlossen, Auslastung-%)
  - Prozentbalken mit Farbkodierung analog zur Liste

---

## Filter-Konzept (Globale Anforderung)
Filter werden in allen Listen-Ansichten konsequent umgesetzt. Die Filterwerte werden als URL-Parameter übergeben, damit Links teilbar und bookmarkbar sind.

### Aufgaben-Filter (`/aufgaben`)
- [x] Filter nach Team
- [x] Filter nach Mitarbeiter
- [x] Filter nach Projekt
- [x] Filter nach Status (OFFEN, IN_BEARBEITUNG, ABGESCHLOSSEN)
- [x] Filter nach Priorität (NIEDRIG, MITTEL, HOCH, KRITISCH)
- [x] Filter nach Fälligkeit (heute, diese Woche, überfällig)
- [x] Freitextsuche (Titel / Beschreibung)
- [x] Alle Filter kombinierbar
- [x] „Filter zurücksetzen"-Button

### Mitarbeiter-Filter (`/mitarbeiter`)
- [x] Filter nach Team
- [x] Filter nach Rolle (Freitext)
- [x] Freitextsuche (Name, E-Mail)

### Projekt-Filter (`/projekte`)
- [x] Filter nach Team
- [x] Filter nach Status (GEPLANT, AKTIV, ABGESCHLOSSEN)
- [x] Filter nach Fälligkeit (überfällig, diese Woche)
- [x] Freitextsuche (Name, Beschreibung)

### Todo-Filter (`/todos`)
- [x] Filter nach Priorität
- [x] Filter nach Status (offen, erledigt, überfällig, heute fällig)
- [x] Freitextsuche (Titel)
- [x] „Filter zurücksetzen"-Button

### Technische Umsetzung Filter
- [x] Filterparameter als `@RequestParam` mit Defaultwerten im Controller
- [x] `Specification<T>` (Spring Data JPA) für dynamische Datenbankabfragen
  - [x] `AufgabeSpecification`
  - [x] `ProjektSpecification`
  - [x] `MitarbeiterSpecification`
  - [x] `TodoSpecification`
- [x] Filterformular behält gewählte Werte nach Submit (Thymeleaf `th:value`)
- [x] Aktive Filter als Badges anzeigen mit Einzel-Entfernen-Option

---

## Clean Code Richtlinien (verbindlich für das gesamte Projekt)

### Namensgebung
- Klassen: substantivisch, aussagekräftig (`AufgabeService`, nicht `AufgMgr`)
- Methoden: Verben, selbsterklärend (`findeUeberfaelligeAufgaben()`, nicht `getData()`)
- Variablen: keine Abkürzungen (`mitarbeiter`, nicht `ma` oder `m`)
- Konstanten: UPPER_SNAKE_CASE in eigener Konstantenklasse
- Keine Magic Numbers / Strings – immer Konstanten oder Enums verwenden

### Methoden
- Jede Methode hat genau eine Aufgabe (Single Responsibility)
- Maximale Methodenlänge: 20 Zeilen (Richtwert)
- Maximal 3 Parameter pro Methode – sonst Parameter-Objekt einführen
- Keine tiefen Verschachtelungen – Early Return / Guard Clauses verwenden
- Keine auskommentierten Code-Blöcke im finalen Code

### Klassen & Architektur
- Schichttrennung strikt einhalten: Controller → Service → Repository
- Controller enthält keine Geschäftslogik – nur Request/Response-Handling
- Repository enthält keine Geschäftslogik – nur Datenbankzugriff
- Service enthält keine UI-Logik
- DTOs / FilterDTO für Filterparameter zwischen Controller und Service
- Keine direkten Entity-Objekte im Template – ggf. ViewModels/DTOs verwenden

### Fehlerbehandlung
- Eigene Exception-Klassen (`EntityNotFoundException`, `ValidationException`)
- Globaler `@ControllerAdvice` für einheitliche Fehlerseiten
- Keine leeren Catch-Blöcke
- Keine `System.out.println` – ausschließlich SLF4J Logger verwenden
  - `private static final Logger log = LoggerFactory.getLogger(ClassName.class)`

### Kommentare & Dokumentation
- Code soll selbsterklärend sein – Kommentare nur für das „Warum", nicht das „Was"
- Javadoc für alle public Service-Methoden
- Keine redundanten Kommentare (`// getter für name` über einem Getter)

### Tests
- Unit Tests für alle Service-Methoden (JUnit 5 + Mockito)
- Integrationstests für alle Repository-Methoden (H2 In-Memory)
- Testabdeckung Ziel: ≥ 80 % für Service-Schicht
- Testnamen beschreiben Verhalten: `findeUeberfaelligeAufgaben_gibtNurOffeneZurueck()`

### Code-Formatierung
- Einheitliche Formatierung per Google Java Style Guide oder Checkstyle
- Kein ungenutzter Import, keine ungenutzten Variablen (IDE-Warnings = 0)
- Lombok verwenden für Boilerplate (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`)

---

## UI/UX Anforderungen (verbindlich für das gesamte Projekt)

### Design-Prinzipien
- Modern & aufgeräumt – klare Struktur, viel Weißraum, keine überladenen Seiten
- Konsistentes Design-System – einheitliche Farben, Abstände, Schriftgrößen überall
- Primärfarbe festlegen (z. B. Indigo `#6366f1`) + Akzentfarben für Status/Priorität
- Dunkle Seitenleiste mit hellem Hauptbereich (modernes App-Layout)
- Abgerundete Ecken (`border-radius`) bei Karten, Buttons und Formularen
- Subtile Schatten (`box-shadow`) für Tiefenwirkung bei Karten

### Layout & Navigation
- Fixe Seitenleiste links (250px), Hauptbereich scrollbar
- Aktiver Menüpunkt farblich hervorgehoben
- Badge im Menü für offene Todos und überfällige Aufgaben
- Breadcrumb-Navigation auf Detailseiten
- Responsive: Seitenleiste auf kleinen Bildschirmen einklappbar

### Komponenten
- Karten (Cards) für Todos, Aufgaben und Mitarbeiter-Übersichten
  - Hover-Effekt (leichter Schatten / Heben)
- Statusbadges farbig und einheitlich (Pill-Form)
- Prioritätsbadges mit Farbkodierung (grün / gelb / orange / rot)
- Fortschrittsbalken für Projekte (Bootstrap Progress)
- Avatar-Kreise für Mitarbeiter (Initialen + Farbe)
- Modals für Löschen-Bestätigung statt separater Seite
- Toast-Benachrichtigungen für Erfolgs- und Fehlermeldungen (auto-hide nach 3s)
- Leere Zustände (Empty States) mit Icon + Hinweistext wenn keine Daten vorhanden

### Formulare & Bedienbarkeit
- Formulare so kurz wie möglich – nur notwendige Felder
- Schnell-Anlegen direkt auf der Listenseite (Todo, Aufgabe)
- Inline-Validierung mit verständlichen Fehlermeldungen (kein technisches Kauderwelsch)
- Fälligkeitsdatum mit Datepicker (Bootstrap Datepicker oder HTML5 `type="date"`)
- Dropdowns mit vorausgewählten Standardwerten
- Tastaturnavigation (Tab-Reihenfolge korrekt)
- Bestätigungsdialoge nur wo wirklich nötig (nur beim Löschen)

### Feedback & Interaktion
- Jede Aktion gibt sofortiges visuelles Feedback (Toast, Badge-Update)
- Ladeindikator bei längeren Operationen
- Überfällige Elemente sofort erkennbar (Farbe + Icon ⚠️)
- Erledigte Todos / Aufgaben visuell abgegrenzt (ausgegraut, durchgestrichen)

### Technische UI-Umsetzung
- Bootstrap 5.3 als CSS-Framework
- Bootstrap Icons für einheitliche Ikonografie
- Eigene `custom.css` für projektspezifische Anpassungen (kein Inline-Style-Wildwuchs)
- Thymeleaf Fragments für wiederverwendbare UI-Komponenten
  - `fragments/navbar.html`
  - `fragments/sidebar.html`
  - `fragments/alerts.html`
  - `fragments/cards.html`

---

## Offene Entscheidungen
- [ ] Soll ein Benutzer-Login (Spring Security) ergänzt werden?
- [ ] Sollen Benachrichtigungen bei Fälligkeit implementiert werden?
- [ ] Soll ein Dark Mode unterstützt werden?

---

## Änderungslog

| Datum      | Beschreibung                                                          |
|------------|-----------------------------------------------------------------------|
| 2026-05-08 | Implementierungsplan erstellt, Technologie-Stack festgelegt           |
| 2026-05-08 | Progressdatei angelegt                                                |
| 2026-05-08 | Flyway Migration als eigenen Abschnitt aufgenommen                    |
| 2026-05-08 | Clean Code Richtlinien als verbindlichen Abschnitt aufgenommen        |
| 2026-05-08 | Todo-Modul (Phase 6) mit Farbkennzeichnung hinzugefügt                |
| 2026-05-08 | Phasen 1–6 vollständig implementiert und getestet (alle Routes 200 OK) |
| 2026-05-08 | Java-Version auf 17 angepasst (installierte JDK-Version)              |
| 2026-05-08 | WebConfig mit Entity-Formattern für saubere Form-Bindung hinzugefügt  |
| 2026-05-08 | Phase 7 abgeschlossen: CSV-Export, H2-Profil, Favicon, README          |
| 2026-05-08 | Mitarbeiter-Zuweisung direkt im Team implementiert (Phase 8)            |
| 2026-05-08 | Team-Auslastung (Fortschrittsbalken + Stat-Kacheln) implementiert       |
