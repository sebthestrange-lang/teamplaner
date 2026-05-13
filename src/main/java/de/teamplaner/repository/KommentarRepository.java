package de.teamplaner.repository;

import de.teamplaner.model.Kommentar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KommentarRepository extends JpaRepository<Kommentar, Long> {

    List<Kommentar> findByAufgabeIdOrderByErstelltAmAsc(Long aufgabeId);

    Optional<Kommentar> findByIdAndOrganisationId(Long id, Long organisationId);
}
