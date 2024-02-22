package nl.tudelft.sem.template.event.exceptions;

@SuppressWarnings("serial")
public class InvalidModelException extends Exception {

    public InvalidModelException() {
    }

    public InvalidModelException(String message) {
        super(message);
    }
}
