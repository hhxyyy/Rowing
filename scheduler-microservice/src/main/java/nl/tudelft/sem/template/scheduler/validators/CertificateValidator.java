package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;

import java.util.*;

public class CertificateValidator extends BaseValidator {
    private static final Map<String, Set<String>> certificates = new HashMap<>() {{
            put("8+", Set.of("4+", "C4", "8+"));
            put("4+", Set.of("C4", "4+"));
            put("C4", Set.of("C4"));
        }};

    @Override
    public boolean handle(UserRequest user, EventModel event) {
        String requiredCertificate = event.getRequirements().get("Certificate");
        if (requiredCertificate != null) {
            // If the user doesn't have a certificate, they shouldn't be able to pilot a boat
            if (user.getRequirements() == null) {
                return false;
            }

            String certificate = user.getRequirements().get("Certificate");

            // If the certificate isn't know to us, we don't know their abilities, and they shouldn't be able to pilot a boat
            if (!certificates.containsKey(certificate)) {
                return false;
            }

            // Makes sure that the position in question steers the boat, and then makes sure that the certificate
            // is equal to or better than what is required
            if (this.getPosition(user) == 0 && !certificates.get(certificate).contains(requiredCertificate)) {
                return false;
            }
        }
        return superWrapper(user, event);
    }

    public boolean superWrapper(UserRequest user, EventModel event) {
        return super.checkNext(user, event);
    }

    /**
     * Adds a new certificate.
     *
     * @param certificate the certificate to add
     * @param supersedes  the certificates it supersedes
     * @return all the certificates including the new one
     */
    public static Map<String, Set<String>> addCertificate(String certificate, Set<String> supersedes) {
        Set<String> owningCertificates = new HashSet<>();

        if (supersedes != null) {
            owningCertificates.addAll(supersedes);
        }

        owningCertificates.add(certificate);
        certificates.put(certificate, owningCertificates);
        return certificates;
    }
}