package nl.tudelft.sem.template.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPersonalInformationSetUpModelTest {

    private UserPersonalInformationSetUpModel userPersonalInformationSetUpModel;

    @BeforeEach
    public void setUp() {
        userPersonalInformationSetUpModel = new UserPersonalInformationSetUpModel();
        userPersonalInformationSetUpModel.setCertificate("C4");
        userPersonalInformationSetUpModel.setGender("F");
        userPersonalInformationSetUpModel.setOrganization("TU Delft");
        userPersonalInformationSetUpModel.setProfessional(true);
    }

    @Test
    void isValid() {
        assertTrue(userPersonalInformationSetUpModel.isValid());
        userPersonalInformationSetUpModel.setGender("M");
        assertTrue(userPersonalInformationSetUpModel.isValid());
    }

    @Test
    void isInvalidGender() {
        userPersonalInformationSetUpModel.setGender("X");
        assertFalse(userPersonalInformationSetUpModel.isValid());
    }

    @Test
    void isInvalidCertificate() {
        userPersonalInformationSetUpModel.setCertificate("X");
        assertFalse(userPersonalInformationSetUpModel.isValid());
    }

    @Test
    void isInvalidOrganization() {
        userPersonalInformationSetUpModel.setOrganization("");
        assertFalse(userPersonalInformationSetUpModel.isValid());
    }
}