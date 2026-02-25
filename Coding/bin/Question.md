Question 11: Implement Retry with Exponential Backoff
Implement a retry mechanism with exponential backoff. The method should:
Retry up to maxRetries times
Wait baseDelayMs \* 2^attempt milliseconds between retries (capped at maxDelayMs)
Return the result if successful, or throw the last exception if all retries fail
