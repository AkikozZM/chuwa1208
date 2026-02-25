import java.util.concurrent.Callable;

public class RetryUtil {
    /**
     * Executes the given task with exponential backoff retry.
     *
     * @param task        The task to execute
     * @param maxRetries  Maximum number of retry attempts
     * @param baseDelayMs Base delay in milliseconds (doubles each retry)
     * @param maxDelayMs  Maximum delay in milliseconds (cap)
     * @return The result of the task
     * @throws Exception If all retries fail, throws the last exception
     */
    public static <T> T executeWithRetry(
            Callable<T> task,
            int maxRetries,
            long baseDelayMs,
            long maxDelayMs) throws Exception {

        Exception lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;

                // If last attempt, throw exception
                if (attempt == maxRetries - 1) {
                    throw lastException;
                }

                // Calculate exponential backoff delay
                long delay = baseDelayMs * (1L << attempt);

                // Cap delay at maxDelayMs
                delay = Math.min(delay, maxDelayMs);

                Thread.sleep(delay);
            }
        }

        throw lastException; // should never reach here
    }
}
