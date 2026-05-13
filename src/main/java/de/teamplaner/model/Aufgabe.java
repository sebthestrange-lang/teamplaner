package de.teamplaner.model;

import de.teamplaner.model.enums.AufgabenStatus;
import de.teamplaner.model.enums.Prioritaet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "aufgaben")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Aufgabe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projekt_id")
    private Projekt projekt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mitarbeiter_id")
    private Mitarbeiter mitarbeiter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Column(nullable = false)
    private String titel;

    @Column(columnDefinition = "TEXT")
    private String beschreibung;

    @NotNull(message = "Priorität muss angegeben werden")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioritaet prioritaet;

    @NotNull(message = "Status muss angegeben werden")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AufgabenStatus status;

    @Column(name = "faellig_am")
    private LocalDate faelligAm;

    @CreationTimestamp
    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "abgeschlossen_am")
    private LocalDateTime abgeschlossenAm;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aufgabe_tags",
        joinColumns = @JoinColumn(name = "aufgabe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aufgabe_abhaengigkeiten",
        joinColumns = @JoinColumn(name = "aufgabe_id"),
        inverseJoinColumns = @JoinColumn(name = "blockiert_von_id")
    )
    @Builder.Default
    private Set<Aufgabe> blockiertVon = new HashSet<>();

    public boolean hatOffeneBlocker() {
        return blockiertVon.stream().anyMatch(a -> a.getStatus() != AufgabenStatus.ABGESCHLOSSEN);
    }
}
