package de.teamplanner.dto;

import de.teamplanner.model.enums.TodoPrioritaet;
import lombok.Data;

@Data
public class TodoFilterDTO {
    private TodoPrioritaet prioritaet;
    private String status;
    private String suche;
}
