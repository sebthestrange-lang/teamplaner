package de.teamplanner.service;

import de.teamplanner.dto.ProjektFilterDTO;
import de.teamplanner.exception.EntityNotFoundException;
import de.teamplanner.model.Aufgabe;
import de.teamplanner.model.Mitarbeiter;
import de.teamplanner.model.Projekt;
import de.teamplanner.model.Team;
import de.teamplanner.model.enums.AufgabenStatus;
import de.teamplanner.repository.AufgabeRepository;
import de.teamplanner.repository.ProjektRepository;
import de.teamplanner.specification.ProjektSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjektService {

    private static final Logger log = LoggerFactory.getLogger(ProjektService.class);

    private final ProjektRepository projektRepository;
    private final AufgabeRepository aufgabeRepository;

    /**
     * Alle Projekte.
     */
    public List<Projekt> alleProjekte() {
        return projektRepository.findAll();
    }

    /**
     * Projekte gefiltert.
     */
    public List<Projekt> mitFilter(ProjektFilterDTO filter) {
        return projektRepository.findAll(ProjektSpecification.withFilter(filter));
    }

    /**
     * Alle Projekte eines Teams.
     */
    public List<Projekt> findByTeam(Team team) {
        return projektRepository.findByTeam(team);
    }

    /**
     * Projekt anhand ID suchen.
     */
    public Optional<Projekt> findById(Long id) {
        return projektRepository.findById(id);
    }

    /**
     * Projekt anhand ID oder Exception.
     */
    public Projekt findByIdOrThrow(Long id) {
        return projektRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projekt", id));
    }

    /**
     * Projekt anlegen oder aktualisieren.
     */
    @Transactional
    public Projekt speichern(Projekt projekt) {
        log.debug("Speichere Projekt: {}", projekt.getName());
        return projektRepository.save(projekt);
    }

    /**
     * Projekt löschen.
     */
    @Transactional
    public void loeschen(Long id) {
        log.debug("Lösche Projekt mit ID {}", id);
        projektRepository.deleteById(id);
    }

    /**
     * Alle Mitarbeiter ermitteln, die Aufgaben in diesem Projekt haben.
     */
    public List<Mitarbeiter> getBeteiligteMitarbeiter(Long projektId) {
        return aufgabeRepository.findByProjektId(projektId)
                .stream()
                .map(Aufgabe::getMitarbeiter)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Aufgaben-Statistik je Mitarbeiter: offene und erledigte Aufgaben.
     */
    public Map<Long, Map<String, Long>> getAufgabenStatistikJeMitarbeiter(Long projektId) {
        List<Aufgabe> aufgaben = aufgabeRepository.findByProjektId(projektId);
        Map<Long, Map<String, Long>> statistik = new HashMap<>();

        for (Aufgabe aufgabe : aufgaben) {
            if (aufgabe.getMitarbeiter() == null) continue;
            Long mitarbeiterId = aufgabe.getMitarbeiter().getId();
            statistik.putIfAbsent(mitarbeiterId, new HashMap<>(Map.of("offen", 0L, "erledigt", 0L)));
            if (aufgabe.getStatus() == AufgabenStatus.ABGESCHLOSSEN) {
                statistik.get(mitarbeiterId).merge("erledigt", 1L, Long::sum);
            } else {
                statistik.get(mitarbeiterId).merge("offen", 1L, Long::sum);
            }
        }
        return statistik;
    }

    public long anzahl() {
        return projektRepository.count();
    }
}
