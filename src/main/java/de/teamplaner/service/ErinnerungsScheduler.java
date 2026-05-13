package de.teamplaner.service;

import de.teamplaner.model.Aufgabe;
import de.teamplaner.model.enums.AufgabenStatus;
import de.teamplaner.repository.AufgabeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ErinnerungsScheduler {

    private static final Logger log = LoggerFactory.getLogger(ErinnerungsScheduler.class);

    private final AufgabeRepository aufgabeRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendeTagliicheErinnerungen() {
        if (!emailService.isKonfiguriert()) {
            log.debug("E-Mail nicht konfiguriert – Erinnerungs-Job übersprungen");
            return;
        }

        LocalDate heute = LocalDate.now();
        LocalDate gestern = heute.minusDays(1);

        Specification<Aufgabe> faellig = (root, query, cb) -> cb.and(
            cb.between(root.get("faelligAm"), gestern, heute),
            cb.notEqual(root.get("status"), AufgabenStatus.ABGESCHLOSSEN)
        );

        List<Aufgabe> aufgaben = aufgabeRepository.findAll(faellig);
        log.info("Erinnerungs-Job: {} fällige Aufgaben gefunden", aufgaben.size());

        for (Aufgabe aufgabe : aufgaben) {
            if (aufgabe.getMitarbeiter() == null) continue;
            if (!aufgabe.getMitarbeiter().isBenachrichtigungenAktiv()) continue;
            String email = aufgabe.getMitarbeiter().getEmail();
            if (email == null || email.isBlank()) continue;

            emailService.sendeErinnerung(email, aufgabe.getTitel(), aufgabe.getFaelligAm());
        }
    }
}
