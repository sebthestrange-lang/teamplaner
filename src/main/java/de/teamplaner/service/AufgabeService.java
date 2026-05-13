package de.teamplaner.service;

import de.teamplaner.config.OrgContext;
import de.teamplaner.dto.AufgabeFilterDTO;
import de.teamplaner.dto.TeamAuslastungDTO;
import de.teamplaner.exception.EntityNotFoundException;
import de.teamplaner.model.Aufgabe;
import de.teamplaner.model.Mitarbeiter;
import de.teamplaner.model.Team;
import de.teamplaner.model.enums.AufgabenStatus;
import de.teamplaner.model.enums.Prioritaet;
import de.teamplaner.repository.AufgabeRepository;
import de.teamplaner.specification.AufgabeSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AufgabeService {

    private static final Logger log = LoggerFactory.getLogger(AufgabeService.class);

    private final AufgabeRepository aufgabeRepository;
    private final OrgContext orgContext;
    private final AuditService auditService;

    private Specification<Aufgabe> byOrg() {
        Long orgId = orgContext.getOrgId();
        return (root, query, cb) -> cb.equal(root.get("organisation").get("id"), orgId);
    }

    public List<Aufgabe> alleAufgaben() {
        return aufgabeRepository.findAll(byOrg());
    }

    public List<Aufgabe> mitFilter(AufgabeFilterDTO filter) {
        return aufgabeRepository.findAll(byOrg().and(AufgabeSpecification.withFilter(filter)));
    }

    public Map<AufgabenStatus, List<Aufgabe>> boardView(AufgabeFilterDTO filter) {
        List<Aufgabe> alle = aufgabeRepository.findAll(
                byOrg().and(AufgabeSpecification.withFilterIgnoreStatus(filter)));
        return alle.stream().collect(Collectors.groupingBy(Aufgabe::getStatus));
    }

    public List<Aufgabe> suchen(String q) {
        if (q == null || q.isBlank()) return List.of();
        String muster = "%" + q.toLowerCase() + "%";
        return aufgabeRepository.findAll(byOrg().and(
            (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("titel")), muster),
                cb.like(cb.lower(root.get("beschreibung")), muster)
            )
        ));
    }

    @Transactional
    public void abhaengigkeitHinzufuegen(Long aufgabeId, Long blockiertVonId) {
        if (aufgabeId.equals(blockiertVonId)) return;
        Aufgabe aufgabe = findByIdOrThrow(aufgabeId);
        Aufgabe blocker = findByIdOrThrow(blockiertVonId);
        if (wuerdeZyklusErstellen(aufgabe, blocker)) return;
        aufgabe.getBlockiertVon().add(blocker);
        aufgabeRepository.save(aufgabe);
    }

    @Transactional
    public void abhaengigkeitEntfernen(Long aufgabeId, Long blockiertVonId) {
        Aufgabe aufgabe = findByIdOrThrow(aufgabeId);
        Aufgabe blocker = findByIdOrThrow(blockiertVonId);
        aufgabe.getBlockiertVon().remove(blocker);
        aufgabeRepository.save(aufgabe);
    }

    private boolean wuerdeZyklusErstellen(Aufgabe aufgabe, Aufgabe neuerBlocker) {
        return istErreichbar(neuerBlocker, aufgabe.getId(), new java.util.HashSet<>());
    }

    private boolean istErreichbar(Aufgabe von, Long zielId, java.util.Set<Long> besucht) {
        if (von.getId().equals(zielId)) return true;
        if (!besucht.add(von.getId())) return false;
        for (Aufgabe dep : von.getBlockiertVon()) {
            if (istErreichbar(dep, zielId, besucht)) return true;
        }
        return false;
    }

    public List<Aufgabe> findByProjektId(Long projektId) {
        return aufgabeRepository.findByProjektId(projektId);
    }

    public List<Aufgabe> findByMitarbeiter(Mitarbeiter mitarbeiter) {
        return aufgabeRepository.findByMitarbeiter(mitarbeiter);
    }

    public Optional<Aufgabe> findById(Long id) {
        return aufgabeRepository.findByIdAndOrganisationId(id, orgContext.getOrgId());
    }

    public Aufgabe findByIdOrThrow(Long id) {
        return aufgabeRepository.findByIdAndOrganisationId(id, orgContext.getOrgId())
                .orElseThrow(() -> new EntityNotFoundException("Aufgabe", id));
    }

    @Transactional
    public Aufgabe speichern(Aufgabe aufgabe) {
        boolean isNeu = aufgabe.getId() == null;
        if (isNeu) {
            aufgabe.setOrganisation(orgContext.getOrganisation());
        }
        log.debug("Speichere Aufgabe: {}", aufgabe.getTitel());
        Aufgabe gespeichert = aufgabeRepository.save(aufgabe);
        auditService.log("Aufgabe", gespeichert.getId(), isNeu ? "CREATE" : "UPDATE");
        return gespeichert;
    }

    @Transactional
    public Aufgabe statusAendern(Long id, AufgabenStatus neuerStatus) {
        Aufgabe aufgabe = findByIdOrThrow(id);
        return statusAendern(aufgabe, neuerStatus);
    }

    @Transactional
    public Aufgabe statusAendern(Aufgabe aufgabe, AufgabenStatus neuerStatus) {
        aufgabe.setStatus(neuerStatus);
        if (neuerStatus == AufgabenStatus.ABGESCHLOSSEN && aufgabe.getAbgeschlossenAm() == null) {
            aufgabe.setAbgeschlossenAm(LocalDateTime.now());
        } else if (neuerStatus != AufgabenStatus.ABGESCHLOSSEN) {
            aufgabe.setAbgeschlossenAm(null);
        }
        Aufgabe gespeichert = aufgabeRepository.save(aufgabe);
        auditService.log("Aufgabe", aufgabe.getId(), "UPDATE");
        return gespeichert;
    }

    @Transactional
    public Aufgabe schnellAnlegen(String titel) {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setTitel(titel.trim());
        aufgabe.setStatus(AufgabenStatus.OFFEN);
        aufgabe.setPrioritaet(Prioritaet.MITTEL);
        aufgabe.setOrganisation(orgContext.getOrganisation());
        Aufgabe gespeichert = aufgabeRepository.save(aufgabe);
        auditService.log("Aufgabe", gespeichert.getId(), "CREATE");
        return gespeichert;
    }

    @Transactional
    public void loeschen(Long id) {
        findByIdOrThrow(id);
        log.debug("Lösche Aufgabe mit ID {}", id);
        aufgabeRepository.deleteById(id);
        auditService.log("Aufgabe", id, "DELETE");
    }

    public boolean isUeberfaellig(Aufgabe aufgabe) {
        return aufgabe.getFaelligAm() != null
                && aufgabe.getFaelligAm().isBefore(LocalDate.now())
                && aufgabe.getStatus() != AufgabenStatus.ABGESCHLOSSEN;
    }

    public List<Aufgabe> heuteFaellig() {
        Specification<Aufgabe> spec = byOrg().and(
                (root, query, cb) -> cb.equal(root.get("faelligAm"), LocalDate.now()));
        return aufgabeRepository.findAll(spec);
    }

    public List<Aufgabe> letzteAktivitaeten(int anzahl) {
        return aufgabeRepository.findAll(byOrg(),
                PageRequest.of(0, anzahl, Sort.by(Sort.Direction.DESC, "erstelltAm"))).getContent();
    }

    public long anzahlOffen() {
        Long orgId = orgContext.getOrgId();
        return aufgabeRepository.countByOrganisationIdAndStatus(orgId, AufgabenStatus.OFFEN)
             + aufgabeRepository.countByOrganisationIdAndStatus(orgId, AufgabenStatus.IN_BEARBEITUNG);
    }

    public long anzahlUeberfaellig() {
        return aufgabeRepository.countByOrganisationIdAndUeberfaellig(
                orgContext.getOrgId(), LocalDate.now(), AufgabenStatus.ABGESCHLOSSEN);
    }

    public long anzahlNachStatus(AufgabenStatus status) {
        return aufgabeRepository.countByOrganisationIdAndStatus(orgContext.getOrgId(), status);
    }

    public TeamAuslastungDTO auslastungFuerTeam(Team team) {
        long offen         = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.OFFEN);
        long inBearbeitung = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.IN_BEARBEITUNG);
        long abgeschlossen = aufgabeRepository.countByTeamAndStatus(team, AufgabenStatus.ABGESCHLOSSEN);
        return new TeamAuslastungDTO(offen, inBearbeitung, abgeschlossen);
    }
}
