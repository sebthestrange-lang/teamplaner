ALTER TABLE todos ADD COLUMN prioritaet_zahl INT NOT NULL DEFAULT 5;

UPDATE todos SET prioritaet_zahl = CASE prioritaet
    WHEN 'KRITISCH' THEN 1
    WHEN 'HOCH'     THEN 2
    WHEN 'MITTEL'   THEN 3
    WHEN 'NIEDRIG'  THEN 4
    ELSE 5
END;

ALTER TABLE todos DROP COLUMN prioritaet;
ALTER TABLE todos RENAME COLUMN prioritaet_zahl TO prioritaet;
