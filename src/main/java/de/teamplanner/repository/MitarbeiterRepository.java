package de.teamplanner.repository;

import de.teamplanner.model.Mitarbeiter;
import de.teamplanner.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MitarbeiterRepository extends JpaRepository<Mitarbeiter, Long>,
        JpaSpecificationExecutor<Mitarbeiter> {

    List<Mitarbeiter> findByTeam(Team team);

    List<Mitarbeiter> findByTeamOrderByNachnameAsc(Team team);

    long countByTeam(Team team);

    List<Mitarbeiter> findByTeamIsNullOrTeamNotOrderByNachnameAsc(Team team);
}
