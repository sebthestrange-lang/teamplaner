package de.teamplaner.service;

import de.teamplaner.config.OrgContext;
import de.teamplaner.exception.EntityNotFoundException;
import de.teamplaner.model.Tag;
import de.teamplaner.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final OrgContext orgContext;

    public List<Tag> alleTags() {
        return tagRepository.findByOrganisationIdOrderByNameAsc(orgContext.getOrgId());
    }

    public Tag findByIdOrThrow(Long id) {
        return tagRepository.findByIdAndOrganisationId(id, orgContext.getOrgId())
                .orElseThrow(() -> new EntityNotFoundException("Tag", id));
    }

    @Transactional
    public Tag speichern(Tag tag) {
        if (tag.getId() == null) {
            tag.setOrganisation(orgContext.getOrganisation());
        }
        return tagRepository.save(tag);
    }

    @Transactional
    public void loeschen(Long id) {
        Tag tag = findByIdOrThrow(id);
        tagRepository.delete(tag);
    }
}
