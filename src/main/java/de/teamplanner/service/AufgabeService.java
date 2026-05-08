package de.teamplanner.service;

import de.teamplanner.dto.AufgabeFilterDTO;
import de.teamplanner.dto.TeamAuslastungDTO;
import de.teamplanner.exception.EntityNotFoundException;
import de.teamplanner.model.Aufgabe;
import de.teamplanner.model.Mitarbeiter;
import de.teamplanner.model.Team;
import de.teamplanner.model.enums.AufgabenStatus;
import de.teamplanner.repository.AufgabeRepository;
import de.teamplanner.specification.AufgabeSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AufgabeService {

    private static final Logger log = LoggerFactory.getLogger(AufgabeService.class);

    private final AufgabeRepository aufgabeRepository;

    /**
     * Alle Aufgaben.
     */
    public List<Aufgabe> alleAufgaben() {
        return aufgabeRepository.findAll();
    }

    /**
     * Aufgaben gefiltert.
     */
    public List<Aufgabe> mitFilter(AufgabeFilterDTO filter) {
        return aufgabeRepository.findAll(AufgabeSpecification.withFilter(filter));
    }

    /**
     * Aufgaben eines Projekts.
     */
    public List<Aufgabe> findByProjektId(Long projektId) {
        return aufgabeRepository.findByProjektId(projektId);
    }

    /**
     * Aufgaben eines Mitarbeiters.
     */
    public List<Aufgabe> findByMitarbeiter(Mitarbeiter mitarbeiter) {
        return aufgabeRepository.findByMitarbeiter(mitarbeiter);
    }

    /**
     * Aufgabe anhand ID suchen.
     */
    public Optional<Aufgabe> findById(Long id) {
        return aufgabeRepository.findById(id);
    }

    /**
     * Aufgabe anhand ID oder Exception.
     */
    public Aufgabe findByIdOrThrow(Long id) {
        return aufgabeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aufgabe", id));
    }

    /**
     * Aufgabe anlegen oder aktualisieren.
     */
    @Transactional
    public Aufgabe speichern(Aufgabe aufgabe) {
        log.debug("Speichere Aufgabe: {}", aufgabe.getTitel());
        return aufgabeRepository.save(aufgabe);
    }

    /**
     * Status einer Aufgabe per ID ändern.
     */
    @Transactional
    public Aufgabe statusAendern(Long id, AufgabenStatus neuerStatus) {
        Aufgabe aufgabe = findByIdOrThrow(id);
        return statusAendern(aufgabe, neuerStatus);
    }

    /**
     * Status auf einem bereits geladenen Aufgabe-Objekt setzen.
     */
    @Transactional
    public Aufgabe statusAendern(Aufgabe aufgabe, AufgabenStatus neuerStatus) {
        aufgabe.setStatus(neuerStatus);
        if (neuerStatus == AufgabenStatus.ABGESCHLOSSEN && aufgabe.getAbgeschlossenAm() == null) {
            aufgabe.setAbgeschlossenAm(LocalDateTime.now());
        } else if (neuerStatus != AufgabenStatus.ABGESCHLOSSEN) {
            aufgabe.setAbgeschlossenAm(null);
        }
        return aufgabeRepository.save(aufgabe);
    }

    /**
     * Aufgabe löschen.
     */
    @Transactional
    public void loeschen(Long id) {
        log.debug("Lösche Aufgabe mit ID {}", id);
        aufgabeRepository.deleteById(id);
    }

    public boolean isUeberfaellig(Aufgabe aufgabe) {
        return aufgabe.getFaelligAm() != null
                && aufgabe.getFaelligAm().isBefore(LocalDate.now())
                && aufgabe.getStatus() != AufgabenStatus.ABGESCHLOSSEN;
    }

    public List<Aufgabe> heuteFaellig() {
        return aufgabeRepository.findByFaelligAm(LocalDate.now());
    }

    public List<Aufgabe> letzteAktivitaeten(int anzahl) {
        return aufgabeRepository.findLatest(
                PageRequest.of(0, anzahl, Sort.by(Sort.Direction.DESC, "erstelltAm")));
    }

    public long anzahlOffen() {
        return aufgabeRepository.countByStatus(AufgabenStatus.OFFEN)
             + aufgabeRepository.countByStatus(AufgabenStatus.IN_BEARBEITUNG);
    }

    public long anzahlUeberfaellig() {
        return aufgabeRepository.countByFaelligAmBeforeAndStatusNot(
                LocalDate.now(), AufgabenStatus.ABGESCHLOSSEN);
    }

    public long anzahlNachStatus(AufgabenStatus status) {
        return aufgabeRepository.countByStatus(status);
    }

    public TeamAuslastungDTO auslastungFuerTeam(Team team) {
        long offen        = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.OFFEN);
        long inBearbeitung = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.IN_BEARBEITUNG);
        long abgeschlossen = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.ABGESCHLOSSEN);
        return new TeamAuslastungDTO(offen, inBearbeitung, abgeschlossen);
    }
}
