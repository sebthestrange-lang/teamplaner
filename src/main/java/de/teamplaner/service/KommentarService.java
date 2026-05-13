package de.teamplaner.service;

import de.teamplaner.config.OrgContext;
import de.teamplaner.exception.EntityNotFoundException;
import de.teamplaner.model.Aufgabe;
import de.teamplaner.model.Benutzer;
import de.teamplaner.model.Kommentar;
import de.teamplaner.repository.KommentarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KommentarService {

    private final KommentarRepository kommentarRepository;
    private final OrgContext orgContext;

    public List<Kommentar> fuerAufgabe(Long aufgabeId) {
        return kommentarRepository.findByAufgabeIdOrderByErstelltAmAsc(aufgabeId);
    }

    @Transactional
    public Kommentar anlegen(Aufgabe aufgabe, Benutzer benutzer, String inhalt) {
        Kommentar k = new Kommentar();
        k.setAufgabe(aufgabe);
        k.setBenutzer(benutzer);
        k.setOrganisation(orgContext.getOrganisation());
        k.setInhalt(inhalt.trim());
        return kommentarRepository.save(k);
    }

    @Transactional
    public void loeschen(Long id) {
        Kommentar k = kommentarRepository.findByIdAndOrganisationId(id, orgContext.getOrgId())
                .orElseThrow(() -> new EntityNotFoundException("Kommentar", id));
        kommentarRepository.delete(k);
    }
}
