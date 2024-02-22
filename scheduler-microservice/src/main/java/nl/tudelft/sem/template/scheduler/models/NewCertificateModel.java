package nl.tudelft.sem.template.scheduler.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCertificateModel {
    private String certificate;
    private Set<String> supersedes;

    public boolean isValid() {
        return certificate != null && !certificate.isBlank();
    }
}
