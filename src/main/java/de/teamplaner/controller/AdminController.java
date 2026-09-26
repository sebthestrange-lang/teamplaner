package de.teamplaner.controller;

import de.teamplaner.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final BackupService backupService;

    @GetMapping("/admin/backup")
    public String backupPage(Model model) {
        return "admin/backup";
    }

    @PostMapping("/admin/backup/create")
    public ResponseEntity<?> createBackup() throws Exception {
        Path dataDir = Paths.get(".").toAbsolutePath().normalize().resolve("data");
        Path dump = backupService.createSqlDump(dataDir);
        PathResource resource = new PathResource(dump);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dump.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/admin/backup/restore")
    public String restoreUpload(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        Path target = Paths.get(".").toAbsolutePath().normalize().resolve("data").resolve(file.getOriginalFilename());
        Files.createDirectories(target.getParent());
        try (var in = file.getInputStream()) {
            Files.copy(in, target);
        }
        model.addAttribute("erfolgsMeldung", "Backup hochgeladen. Bitte App stoppen und den SQL-Import manuell ausführen.");
        return "admin/backup";
    }
}
