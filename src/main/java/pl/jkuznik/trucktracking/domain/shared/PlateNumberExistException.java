package pl.jkuznik.trucktracking.domain.shared;

public class PlateNumberExistException extends RuntimeException {

    public PlateNumberExistException() {
    }

    public PlateNumberExistException(String message) {
        super(message);
    }

    public PlateNumberExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlateNumberExistException(Throwable cause) {
        super(cause);
    }

    public PlateNumberExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
