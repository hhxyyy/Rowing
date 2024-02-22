package nl.tudelft.sem.template.user.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;


public class UserPersonalInformationSetUpModel {
    private String certificate;
    private String gender;
    private String organization;
    @JsonProperty("professional")
    private transient boolean isProfessional;

    /**
     * Check if the User input for their personal information is valid.
     */
    public boolean isValid() {
        String[] certificates = {"C4", "4+", "8+"};
        return ("F".equals(gender) || "M".equals(gender))
                && Arrays.asList(certificates).contains(certificate)
                && !organization.isBlank();
    }

    /**
     * Creates a new user information set-up model.
     *
     * @param certificate the certificate of the user
     * @param gender the gender of the user
     * @param organization the organization of the user
     * @param isProfessional boolean repressenting whether the user is
     *                       or is not professional.
     */
    public UserPersonalInformationSetUpModel(String certificate, String gender,
                                             String organization,
                                             boolean isProfessional) {
        this.certificate = certificate;
        this.gender = gender;
        this.organization = organization;
        this.isProfessional = isProfessional;
    }

    public UserPersonalInformationSetUpModel() {
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isUserProfessional() {
        return isProfessional;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }
}
