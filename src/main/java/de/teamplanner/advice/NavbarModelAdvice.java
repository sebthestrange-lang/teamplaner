package de.teamplanner.advice;

import de.teamplanner.model.enums.AufgabenStatus;
import de.teamplanner.repository.AufgabeRepository;
import de.teamplanner.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;

@ControllerAdvice
@RequiredArgsConstructor
public class NavbarModelAdvice {

    private final TodoRepository todoRepository;
    private final AufgabeRepository aufgabeRepository;

    @ModelAttribute("sidebarOffeneTodos")
    public long sidebarOffeneTodos() {
        return todoRepository.countByErledigtFalse();
    }

    @ModelAttribute("sidebarUeberfaelligeAufgaben")
    public long sidebarUeberfaelligeAufgaben() {
        return aufgabeRepository.countByFaelligAmBeforeAndStatusNot(
                LocalDate.now(), AufgabenStatus.ABGESCHLOSSEN);
    }

    @ModelAttribute("currentUri")
    public String currentUri() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getRequestURI();
        } catch (IllegalStateException e) {
            return "";
        }
    }
}
