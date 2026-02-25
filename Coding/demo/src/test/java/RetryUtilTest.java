import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryUtilTest {

    @Test
    void shouldReturnResultOnFirstSuccess() throws Exception {
        String result = RetryUtil.executeWithRetry(
                () -> "success",
                3, 100, 1000);
        assertEquals("success", result);
    }

    @Test
    void shouldRetryAndEventuallySucceed() throws Exception {
        AtomicInteger attempts = new AtomicInteger(0);
        String result = RetryUtil.executeWithRetry(
                () -> {
                    if (attempts.incrementAndGet() < 3) {
                        throw new RuntimeException("Temporary failure");
                    }
                    return "success after retries";
                },
                5, 100, 1000);
        assertEquals("success after retries", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void shouldThrowAfterMaxRetries() {
        AtomicInteger attempts = new AtomicInteger(0);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RetryUtil.executeWithRetry(
                    () -> {
                        attempts.incrementAndGet();
                        throw new RuntimeException("Always fails");
                    },
                    3, 100, 1000);
        });
        assertEquals("Always fails", exception.getMessage());
        assertEquals(3, attempts.get());
    }

    @Test
    void shouldCapDelayAtMaxDelay() throws Exception {
        AtomicInteger attempts = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        RetryUtil.executeWithRetry(
                () -> {
                    if (attempts.incrementAndGet() < 4) {
                        throw new RuntimeException("Fail");
                    }
                    return "done";
                },
                5, 100, 500);
        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue(elapsed >= 700, "Should wait at least 700ms total");
    }

}
