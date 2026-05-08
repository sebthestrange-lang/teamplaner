package de.teamplanner.specification;

import de.teamplanner.dto.MitarbeiterFilterDTO;
import de.teamplanner.model.Mitarbeiter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class MitarbeiterSpecification {

    private MitarbeiterSpecification() {}

    public static Specification<Mitarbeiter> withFilter(MitarbeiterFilterDTO filter) {
        return Specification
                .where(hatTeam(filter.getTeamId()))
                .and(hatRolle(filter.getRolle()))
                .and(enthältSuchtext(filter.getSuche()));
    }

    private static Specification<Mitarbeiter> hatTeam(Long teamId) {
        if (teamId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("team").get("id"), teamId);
    }

    private static Specification<Mitarbeiter> hatRolle(String rolle) {
        if (!StringUtils.hasText(rolle)) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("rolle")), "%" + rolle.toLowerCase() + "%");
    }

    private static Specification<Mitarbeiter> enthältSuchtext(String suche) {
        if (!StringUtils.hasText(suche)) return null;
        String muster = "%" + suche.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("vorname")), muster),
                cb.like(cb.lower(root.get("nachname")), muster),
                cb.like(cb.lower(root.get("email")), muster)
        );
    }
}
