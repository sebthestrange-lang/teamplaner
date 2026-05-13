package de.teamplaner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@teamplaner.de}")
    private String absender;

    public boolean isKonfiguriert() {
        return mailSender != null;
    }

    public void sendeErinnerung(String an, String aufgabeTitel, LocalDate faelligAm) {
        if (mailSender == null) {
            log.debug("E-Mail nicht konfiguriert – überspringe Erinnerung für {}", an);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(absender);
            msg.setTo(an);
            msg.setSubject("Erinnerung: Aufgabe fällig – " + aufgabeTitel);
            msg.setText(
                "Hallo,\n\n" +
                "Ihre Aufgabe \"" + aufgabeTitel + "\" war am " + faelligAm.format(FMT) + " fällig.\n\n" +
                "Bitte melden Sie sich im TeamPlaner an, um die Aufgabe zu bearbeiten.\n\n" +
                "TeamPlaner"
            );
            mailSender.send(msg);
            log.info("Erinnerung gesendet an {}", an);
        } catch (MailException e) {
            log.error("Fehler beim Senden der Erinnerung an {}: {}", an, e.getMessage());
        }
    }
}
