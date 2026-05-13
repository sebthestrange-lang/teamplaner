package de.teamplaner.model.enums;

public enum TodoWiederholung {
    KEINE("Keine"),
    TAEGLICH("Täglich"),
    WOECHENTLICH("Wöchentlich"),
    MONATLICH("Monatlich");

    private final String bezeichnung;

    TodoWiederholung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
}
