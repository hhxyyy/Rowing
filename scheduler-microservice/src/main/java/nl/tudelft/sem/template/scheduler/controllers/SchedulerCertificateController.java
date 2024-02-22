package nl.tudelft.sem.template.scheduler.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.tudelft.sem.template.scheduler.models.NewCertificateModel;
import nl.tudelft.sem.template.scheduler.services.AuthenticationHandler;
import nl.tudelft.sem.template.scheduler.validators.CertificateValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class SchedulerCertificateController {
    private final transient AuthenticationHandler handler;

    public SchedulerCertificateController(AuthenticationHandler handler) {
        this.handler = handler;
    }

    /**
     * Adds a new certificate to the existing map of certificates.
     *
     * @param model the model specifying the certificate to be added
     * @return a JSON string containing all current certificates
     */
    @PostMapping("/addCertificate")
    public ResponseEntity<String> addNewCertificate(@RequestBody NewCertificateModel model) {
        if (!model.isValid()) {
            return ResponseEntity.badRequest().body("No certificate was specified.");
        }

        // Only admins should be allowed to add new certificates
        if (!handler.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Set<String>> certificates = CertificateValidator.addCertificate(model.getCertificate(),
                model.getSupersedes());

        try {
            ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(certificates));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
