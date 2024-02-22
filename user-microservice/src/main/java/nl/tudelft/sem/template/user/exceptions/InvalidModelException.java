package nl.tudelft.sem.template.user.exceptions;

@SuppressWarnings("PMD")
public class InvalidModelException extends RuntimeException {
    public InvalidModelException(String message) {
        super(message);
    }
}
