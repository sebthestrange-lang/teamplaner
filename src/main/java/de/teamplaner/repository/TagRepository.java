package de.teamplaner.repository;

import de.teamplaner.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByOrganisationIdOrderByNameAsc(Long organisationId);

    Optional<Tag> findByIdAndOrganisationId(Long id, Long organisationId);
}
