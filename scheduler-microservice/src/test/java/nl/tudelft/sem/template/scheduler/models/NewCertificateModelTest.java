package nl.tudelft.sem.template.scheduler.models;

import nl.tudelft.sem.template.scheduler.models.NewCertificateModel;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NewCertificateModelTest {

    @Test
    void testInvalidNull() {
        NewCertificateModel model = new NewCertificateModel(null, Set.of());

        assertThat(model.isValid()).isFalse();
    }

    @Test
    void testInvalidBlank() {
        NewCertificateModel model = new NewCertificateModel(" ", Set.of());

        assertThat(model.isValid()).isFalse();
    }

    @Test
    void testValid() {
        NewCertificateModel model = new NewCertificateModel("a", Set.of("b", "c"));

        assertThat(model.isValid()).isTrue();
    }

    @Test
    void testValidSetNull() {
        NewCertificateModel model = new NewCertificateModel("a", null);

        assertThat(model.isValid()).isTrue();
    }
}