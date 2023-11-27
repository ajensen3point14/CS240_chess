package clientUI;



public class MyClientException extends RuntimeException{

    /**
     * Sets error code and populates the message body appropriately
     * @param message message body
     */
    public MyClientException(String message) {
        super(message);
    }
}
