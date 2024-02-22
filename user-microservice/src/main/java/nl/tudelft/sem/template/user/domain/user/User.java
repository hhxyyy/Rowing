package nl.tudelft.sem.template.user.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "users")
@SuppressWarnings("PMD")
public class User {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "certificate", nullable = false)
    private String certificate;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "isProfessional", nullable = false)
    private boolean isProfessional;



    /**
     * Constructor for application user.
     *
     * @param username username (unique)
     * @param certificate rowing certificates
     * @param gender gender
     * @param organization organization the User is a part of
     */
    public User(String username, String certificate, String gender, String organization, boolean isProfessional) {
        this.username = username;
        this.certificate = certificate;
        this.gender = gender;
        this.organization = organization;
        this.isProfessional = isProfessional;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getGender() {
        return gender;
    }


    public String getOrganization() {
        return organization;
    }

    public boolean isUserProfessional() {
        return isProfessional;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return isProfessional == user.isProfessional
                && username.equals(user.username)
                && certificate.equals(user.certificate)
                && gender.equals(user.gender) && organization.equals(user.organization);
    }

    @Override
    public String toString() {
        return "User("
                + "username=" + username
                + ", certificate=" + certificate
                + ", gender=" + gender
                + ", organization=" + organization
                + ", isProfessional=" + isProfessional
                + ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, certificate, gender, organization, isProfessional);
    }
}
