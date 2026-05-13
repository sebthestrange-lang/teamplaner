package de.teamplaner.repository;

import de.teamplaner.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long>,
        JpaSpecificationExecutor<Meeting> {

    List<Meeting> findByOrganisationIdOrderByDatumDesc(Long organisationId);

    Optional<Meeting> findByIdAndOrganisationId(Long id, Long organisationId);
}
