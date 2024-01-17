package kotlin_in_action.lambdas;

public class ComputationUtils {
    public void postponeComputation(int delay, Runnable computation) throws InterruptedException {
        Thread.sleep(delay);
        computation.run();
    }
}
