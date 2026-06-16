package de.teamplaner.controller;

import de.teamplaner.config.OrgContext;
import de.teamplaner.dto.AufgabeFilterDTO;
import de.teamplaner.model.Aufgabe;
import de.teamplaner.model.enums.AufgabenStatus;
import de.teamplaner.model.enums.Prioritaet;
import de.teamplaner.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/aufgaben")
@RequiredArgsConstructor
public class AufgabeController {

    private final AufgabeService aufgabeService;
    private final ProjektService projektService;
    private final MitarbeiterService mitarbeiterService;
    private final TeamService teamService;
    private final TagService tagService;
    private final KommentarService kommentarService;
    private final BenutzerService benutzerService;
    private final OrgContext orgContext;

    @GetMapping
    public String liste(@ModelAttribute AufgabeFilterDTO filter, Model model) {
        model.addAttribute("aufgaben", aufgabeService.mitFilter(filter));
        model.addAttribute("teams", teamService.alleTeams());
        model.addAttribute("projekte", projektService.alleProjekte());
        model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
        model.addAttribute("aufgabenStatus", AufgabenStatus.values());
        model.addAttribute("prioritaeten", Prioritaet.values());
        model.addAttribute("tags", tagService.alleTags());
        model.addAttribute("filter", filter);
        return "aufgaben/liste";
    }

    @GetMapping("/board")
    public String board(@ModelAttribute AufgabeFilterDTO filter, Model model) {
        Map<AufgabenStatus, List<Aufgabe>> board = aufgabeService.boardView(filter);
        model.addAttribute("offeneAufgaben", board.getOrDefault(AufgabenStatus.OFFEN, List.of()));
        model.addAttribute("inBearbeitungAufgaben", board.getOrDefault(AufgabenStatus.IN_BEARBEITUNG, List.of()));
        model.addAttribute("abgeschlosseneAufgaben", board.getOrDefault(AufgabenStatus.ABGESCHLOSSEN, List.of()));
        model.addAttribute("teams", teamService.alleTeams());
        model.addAttribute("projekte", projektService.alleProjekte());
        model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
        model.addAttribute("tags", tagService.alleTags());
        model.addAttribute("filter", filter);
        return "aufgaben/board";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Aufgabe aufgabe = aufgabeService.findByIdOrThrow(id);
        model.addAttribute("aufgabe", aufgabe);
        model.addAttribute("kommentare", kommentarService.fuerAufgabe(id));
        model.addAttribute("alleAufgaben", aufgabeService.alleAufgaben().stream()
                .filter(a -> !a.getId().equals(id))
                .toList());
        return "aufgaben/detail";
    }

    @GetMapping("/neu")
    public String neuFormular(@RequestParam(required = false) Long projektId, Model model) {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setStatus(AufgabenStatus.OFFEN);
        aufgabe.setPrioritaet(Prioritaet.MITTEL);
        if (projektId != null) {
            aufgabe.setProjekt(projektService.findByIdOrThrow(projektId));
        }
        model.addAttribute("aufgabe", aufgabe);
        model.addAttribute("projekte", projektService.alleProjekte());
        model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
        model.addAttribute("aufgabenStatus", AufgabenStatus.values());
        model.addAttribute("prioritaeten", Prioritaet.values());
        model.addAttribute("alleTags", tagService.alleTags());
        model.addAttribute("aktion", "Neue Aufgabe");
        return "aufgaben/formular";
    }

    @PostMapping
    public String erstellen(@Valid @ModelAttribute("aufgabe") Aufgabe aufgabe,
                            BindingResult result,
                            @RequestParam(required = false) List<Long> tagIds,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("projekte", projektService.alleProjekte());
            model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
            model.addAttribute("aufgabenStatus", AufgabenStatus.values());
            model.addAttribute("prioritaeten", Prioritaet.values());
            model.addAttribute("alleTags", tagService.alleTags());
            model.addAttribute("aktion", "Neue Aufgabe");
            return "aufgaben/formular";
        }
        if (tagIds != null) {
            tagIds.stream().map(tagService::findByIdOrThrow).forEach(aufgabe.getTags()::add);
        }
        aufgabeService.speichern(aufgabe);
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Aufgabe wurde erstellt.");
        return "redirect:/aufgaben";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeitenFormular(@PathVariable Long id, Model model) {
        model.addAttribute("aufgabe", aufgabeService.findByIdOrThrow(id));
        model.addAttribute("projekte", projektService.alleProjekte());
        model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
        model.addAttribute("aufgabenStatus", AufgabenStatus.values());
        model.addAttribute("prioritaeten", Prioritaet.values());
        model.addAttribute("alleTags", tagService.alleTags());
        model.addAttribute("aktion", "Aufgabe bearbeiten");
        return "aufgaben/formular";
    }

    @PostMapping("/{id}")
    public String aktualisieren(@PathVariable Long id,
                                @Valid @ModelAttribute("aufgabe") Aufgabe formDaten,
                                BindingResult result,
                                @RequestParam(required = false) List<Long> tagIds,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("projekte", projektService.alleProjekte());
            model.addAttribute("mitarbeiter", mitarbeiterService.alleMitarbeiter());
            model.addAttribute("aufgabenStatus", AufgabenStatus.values());
            model.addAttribute("prioritaeten", Prioritaet.values());
            model.addAttribute("alleTags", tagService.alleTags());
            model.addAttribute("aktion", "Aufgabe bearbeiten");
            return "aufgaben/formular";
        }
        Aufgabe aufgabe = aufgabeService.findByIdOrThrow(id);
        aufgabe.setTitel(formDaten.getTitel());
        aufgabe.setBeschreibung(formDaten.getBeschreibung());
        aufgabe.setPrioritaet(formDaten.getPrioritaet());
        aufgabe.setProjekt(formDaten.getProjekt());
        aufgabe.setMitarbeiter(formDaten.getMitarbeiter());
        aufgabe.setFaelligAm(formDaten.getFaelligAm());
        aufgabe.getTags().clear();
        if (tagIds != null) {
            tagIds.stream().map(tagService::findByIdOrThrow).forEach(aufgabe.getTags()::add);
        }
        aufgabeService.statusAendern(aufgabe, formDaten.getStatus());
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Aufgabe wurde aktualisiert.");
        return "redirect:/aufgaben/" + id;
    }

    @PostMapping("/{id}/kommentar")
    public String kommentarHinzufuegen(@PathVariable Long id,
                                       @RequestParam String inhalt) {
        if (inhalt != null && !inhalt.isBlank()) {
            Aufgabe aufgabe = aufgabeService.findByIdOrThrow(id);
            kommentarService.anlegen(aufgabe, benutzerService.aktuellerBenutzer(), inhalt);
        }
        return "redirect:/aufgaben/" + id;
    }

    @PostMapping("/{id}/kommentar/{kommentarId}/loeschen")
    public String kommentarLoeschen(@PathVariable Long id, @PathVariable Long kommentarId) {
        kommentarService.loeschen(kommentarId);
        return "redirect:/aufgaben/" + id;
    }

    @PostMapping("/{id}/abhaengigkeit")
    public String abhaengigkeitHinzufuegen(@PathVariable Long id,
                                           @RequestParam(required = false) Long blockiertVonId) {
        if (blockiertVonId != null) {
            aufgabeService.abhaengigkeitHinzufuegen(id, blockiertVonId);
        }
        return "redirect:/aufgaben/" + id;
    }

    @PostMapping("/{id}/abhaengigkeit/{blockiertVonId}/loeschen")
    public String abhaengigkeitEntfernen(@PathVariable Long id,
                                         @PathVariable Long blockiertVonId) {
        aufgabeService.abhaengigkeitEntfernen(id, blockiertVonId);
        return "redirect:/aufgaben/" + id;
    }

    @PostMapping("/{id}/status")
    public String statusAendern(@PathVariable Long id,
                                @RequestParam AufgabenStatus status,
                                HttpServletRequest request) {
        aufgabeService.statusAendern(id, status);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/aufgaben");
    }

    @PostMapping("/schnell")
    public String schnellAnlegen(@RequestParam String titel) {
        if (titel != null && !titel.isBlank()) {
            aufgabeService.schnellAnlegen(titel);
        }
        return "redirect:/aufgaben";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        aufgabeService.loeschen(id);
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Aufgabe wurde gelöscht.");
        return "redirect:/aufgaben";
    }

    @GetMapping("/export")
    public void csvExport(@ModelAttribute AufgabeFilterDTO filter,
                          HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"aufgaben.csv\"");

        List<Aufgabe> aufgaben = aufgabeService.mitFilter(filter);
        PrintWriter writer = response.getWriter();
        writer.println("ID,Titel,Projekt,Mitarbeiter,Priorität,Status,Fällig am,Erstellt am");

        for (Aufgabe a : aufgaben) {
            writer.println(String.join(";",
                    String.valueOf(a.getId()),
                    csv(a.getTitel()),
                    csv(a.getProjekt() != null ? a.getProjekt().getName() : ""),
                    csv(a.getMitarbeiter() != null ? a.getMitarbeiter().getVollstaendigerName() : ""),
                    csv(a.getPrioritaet().getBezeichnung()),
                    csv(a.getStatus().getBezeichnung()),
                    a.getFaelligAm() != null ? a.getFaelligAm().toString() : "",
                    a.getErstelltAm() != null ? a.getErstelltAm().toLocalDate().toString() : ""
            ));
        }
        writer.flush();
    }

    private String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
