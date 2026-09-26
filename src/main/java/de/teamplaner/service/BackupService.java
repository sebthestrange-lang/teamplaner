package de.teamplaner.service;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Script;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BackupService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public Path createSqlDump(Path dataDir) throws Exception {
        String ts = LocalDateTime.now().format(TS);
        File out = dataDir.resolve("backup_" + ts + ".sql").toFile();
        // Use H2 Script tool to write a SQL dump
        Script.main(new String[]{"-url", "jdbc:h2:file:" + dataDir.resolve("teamplaner").toString(),
                "-user", "sa", "-password", "", "-script", out.getAbsolutePath()});
        return out.toPath();
    }
}
