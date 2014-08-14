package simplify.vm;

public class MaxCallDepthExceeded extends Exception {

    private static final long serialVersionUID = -9148536615221065528L;

    MaxCallDepthExceeded(String message) {
        super(message);
    }
}
