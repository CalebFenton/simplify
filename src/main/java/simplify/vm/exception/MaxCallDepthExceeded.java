package simplify.vm.exception;

public class MaxCallDepthExceeded extends Exception {

    private static final long serialVersionUID = -9148536615221065528L;

    public MaxCallDepthExceeded(String message) {
        super(message);
    }
}
