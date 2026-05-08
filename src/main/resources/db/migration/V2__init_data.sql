-- V2: Initiale Testdaten (Entwicklung)
-- WICHTIG: Diese Datei niemals nachträglich ändern – immer neue Version anlegen!

INSERT INTO teams (name, farbe) VALUES
    ('Backend',  '#6366f1'),
    ('Frontend', '#10b981'),
    ('Design',   '#f59e0b');

INSERT INTO mitarbeiter (team_id, vorname, nachname, rolle, email) VALUES
    (1, 'Anna',   'Müller',    'Senior Developer',  'anna.mueller@example.com'),
    (1, 'Bernd',  'Schmidt',   'Junior Developer',  'bernd.schmidt@example.com'),
    (2, 'Clara',  'Weber',     'Frontend Lead',     'clara.weber@example.com'),
    (2, 'David',  'Fischer',   'UI Developer',      'david.fischer@example.com'),
    (3, 'Elena',  'Hofmann',   'UX Designer',       'elena.hofmann@example.com');

INSERT INTO projekte (team_id, name, beschreibung, status, farbe, faellig_am) VALUES
    (1, 'API Refactoring',    'Modernisierung der REST-API',       'AKTIV',        '#6366f1', DATEADD('DAY', 30, CURRENT_DATE)),
    (2, 'Neues Dashboard',    'Redesign der Hauptansicht',         'AKTIV',        '#10b981', DATEADD('DAY', 14, CURRENT_DATE)),
    (3, 'Design System',      'Aufbau eines einheitlichen DS',     'GEPLANT',      '#f59e0b', DATEADD('DAY', 60, CURRENT_DATE)),
    (1, 'Auth-Modul',         'OAuth2 Integration',                'ABGESCHLOSSEN','#8b5cf6', DATEADD('DAY', -7, CURRENT_DATE));

INSERT INTO aufgaben (projekt_id, mitarbeiter_id, titel, prioritaet, status, faellig_am) VALUES
    (1, 1, 'Endpunkte dokumentieren',       'HOCH',     'IN_BEARBEITUNG', DATEADD('DAY', 5,   CURRENT_DATE)),
    (1, 2, 'Unit Tests schreiben',           'MITTEL',   'OFFEN',          DATEADD('DAY', 10,  CURRENT_DATE)),
    (1, 1, 'Datenbankabfragen optimieren',   'KRITISCH', 'OFFEN',          DATEADD('DAY', -2,  CURRENT_DATE)),
    (2, 3, 'Komponenten erstellen',          'HOCH',     'IN_BEARBEITUNG', DATEADD('DAY', 7,   CURRENT_DATE)),
    (2, 4, 'Responsive Layouts',             'MITTEL',   'OFFEN',          DATEADD('DAY', 12,  CURRENT_DATE)),
    (2, 3, 'Wireframes umsetzen',            'HOCH',     'ABGESCHLOSSEN',  DATEADD('DAY', -3,  CURRENT_DATE)),
    (3, 5, 'Farbpalette definieren',         'MITTEL',   'IN_BEARBEITUNG', DATEADD('DAY', 20,  CURRENT_DATE)),
    (3, 5, 'Typografie festlegen',           'NIEDRIG',  'OFFEN',          DATEADD('DAY', 25,  CURRENT_DATE)),
    (4, 1, 'JWT-Implementierung',            'KRITISCH', 'ABGESCHLOSSEN',  DATEADD('DAY', -10, CURRENT_DATE)),
    (4, 2, 'Login-Tests',                    'HOCH',     'ABGESCHLOSSEN',  DATEADD('DAY', -8,  CURRENT_DATE));

INSERT INTO todos (titel, prioritaet, erledigt, faellig_am) VALUES
    ('Projektmeeting vorbereiten',  'HOCH',     FALSE, CURRENT_DATE),
    ('Code Review durchführen',     'MITTEL',   FALSE, DATEADD('DAY', 2, CURRENT_DATE)),
    ('Deployment-Skript prüfen',    'KRITISCH', FALSE, DATEADD('DAY', -1, CURRENT_DATE)),
    ('Backlog aufräumen',           'NIEDRIG',  FALSE, DATEADD('DAY', 7, CURRENT_DATE)),
    ('Readme aktualisieren',        'NIEDRIG',  TRUE,  DATEADD('DAY', -5, CURRENT_DATE));
