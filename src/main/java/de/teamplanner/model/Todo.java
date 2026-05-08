package de.teamplanner.model;

import de.teamplanner.model.enums.TodoPrioritaet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Column(nullable = false)
    private String titel;

    @Column(columnDefinition = "TEXT")
    private String beschreibung;

    @NotNull(message = "Priorität muss angegeben werden")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoPrioritaet prioritaet;

    @Builder.Default
    @Column(nullable = false)
    private boolean erledigt = false;

    @Column(name = "faellig_am")
    private LocalDate faelligAm;

    @CreationTimestamp
    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "erledigt_am")
    private LocalDateTime erledigtAm;
}
