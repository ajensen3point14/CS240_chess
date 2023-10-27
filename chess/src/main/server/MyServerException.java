package server;

public class MyServerException extends RuntimeException{
    int httpErrorCode;
    public MyServerException(String message, int errorCode) {
        super(message);
        this.httpErrorCode = errorCode;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }
}
