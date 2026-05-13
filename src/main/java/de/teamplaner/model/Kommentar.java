package de.teamplaner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kommentare")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Kommentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aufgabe_id", nullable = false)
    private Aufgabe aufgabe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "benutzer_id", nullable = false)
    private Benutzer benutzer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String inhalt;

    @CreationTimestamp
    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm;
}
