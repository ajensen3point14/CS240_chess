package server;

/**
 * Exception that will be thrown if the server encounters an error from this program
 */
public class MyServerException extends RuntimeException {
    int httpErrorCode;

    /**
     * Sets error code and populates the message body appropriately
     * @param message message body
     * @param errorCode error code provided by the code
     */
    public MyServerException(String message, int errorCode) {
        super(message);
        this.httpErrorCode = errorCode;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }
}
