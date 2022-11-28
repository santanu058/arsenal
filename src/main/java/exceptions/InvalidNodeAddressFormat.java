package exceptions;

public class InvalidNodeAddressFormat extends RuntimeException {
    public InvalidNodeAddressFormat(String message) {
        super(message);
    }
}
