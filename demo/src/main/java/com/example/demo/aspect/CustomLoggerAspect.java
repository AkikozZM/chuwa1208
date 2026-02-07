package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class CustomLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoggerAspect.class);
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * Before advice - logs before method execution
     * Binds joinPoint directly without using empty method
     * Logs both internal code (com.example.demo) and external code (Spring
     * framework, Jakarta, etc.)
     */
    @Before("execution(* com.example.demo..*(..)) || " +
            "execution(* org.springframework.web..*(..)) || " +
            "execution(* org.springframework.http..*(..)) || " +
            "execution(* jakarta.servlet..*(..)) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("🔵 BEFORE METHOD EXECUTION");
        logger.info("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        logger.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));

        // Log REST API request details if available
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                logger.info("REST API Request Details:");
                logger.info("  URL: {} {}", request.getMethod(), request.getRequestURL());
                logger.info("  Remote Address: {}", request.getRemoteAddr());
                logger.info("  Headers: {}", getRequestHeaders(request));
                logger.info("  Query String: {}", request.getQueryString());
            }
        } catch (Exception e) {
            // Not a web request, ignore
        }

        startTime.set(System.currentTimeMillis());
        logger.info("═══════════════════════════════════════════════════════════");
    }

    /**
     * After advice - logs after method execution (always executes)
     * Binds joinPoint directly - logs both internal and external code
     */
    @After("execution(* com.example.demo..*(..)) || " +
            "execution(* org.springframework..*(..)) || " +
            "execution(* jakarta.servlet..*(..)) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void logAfter(JoinPoint joinPoint) {
        Long executionTime = startTime.get() != null ? System.currentTimeMillis() - startTime.get() : null;

        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("🟢 AFTER METHOD EXECUTION (Always executes)");
        logger.info("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        if (executionTime != null) {
            logger.info("Execution Time: {} ms", executionTime);
        }
        logger.info("═══════════════════════════════════════════════════════════");
    }

    /**
     * AfterReturning advice - logs on successful method execution
     * Binds joinPoint directly - logs both internal and external code
     */
    @AfterReturning(pointcut = "execution(* com.example.demo..*(..)) || " +
            "execution(* org.springframework..*(..)) || " +
            "execution(* jakarta.servlet..*(..)) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Long executionTime = startTime.get() != null ? System.currentTimeMillis() - startTime.get() : null;

        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("✅ AFTER RETURNING (On Success)");
        logger.info("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        logger.info("Return Value: {}", result != null ? result.toString() : "null");

        // Log REST API response details if available
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                logger.info("REST API Response Details:");
                logger.info("  Status: Success");
                logger.info("  Response Body: {}", result);
            }
        } catch (Exception e) {
            // Not a web request, ignore
        }

        if (executionTime != null) {
            logger.info("Execution Time: {} ms", executionTime);
        }
        logger.info("═══════════════════════════════════════════════════════════");

        startTime.remove();
    }

    /**
     * AfterThrowing advice - logs on exception
     * Binds joinPoint directly - logs both internal and external code
     */
    @AfterThrowing(pointcut = "execution(* com.example.demo..*(..)) || " +
            "execution(* org.springframework..*(..)) || " +
            "execution(* jakarta.servlet..*(..)) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        Long executionTime = startTime.get() != null ? System.currentTimeMillis() - startTime.get() : null;

        logger.error("═══════════════════════════════════════════════════════════");
        logger.error("❌ AFTER THROWING (On Exception)");
        logger.error("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        logger.error("Exception: {}", exception.getClass().getName());
        logger.error("Exception Message: {}", exception.getMessage());
        logger.error("Stack Trace:", exception);

        // Log REST API error response details if available
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                logger.error("REST API Error Response Details:");
                logger.error("  Status: Error");
                logger.error("  Exception Type: {}", exception.getClass().getName());
            }
        } catch (Exception e) {
            // Not a web request, ignore
        }

        if (executionTime != null) {
            logger.error("Execution Time: {} ms", executionTime);
        }
        logger.error("═══════════════════════════════════════════════════════════");

        startTime.remove();
    }

    /**
     * Around advice - logs method execution time and provides full control
     * Binds joinPoint directly - logs both internal and external code
     */
    @Around("execution(* com.example.demo..*(..)) || " +
            "execution(* org.springframework..*(..)) || " +
            "execution(* jakarta.servlet..*(..)) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("🔄 AROUND ADVICE - Method Execution Started");
        logger.info("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        logger.info("═══════════════════════════════════════════════════════════");

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            logger.info("═══════════════════════════════════════════════════════════");
            logger.info("🔄 AROUND ADVICE - Method Execution Completed");
            logger.info("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            logger.info("Execution Time: {} ms", executionTime);
            logger.info("Return Value: {}", result != null ? result.toString() : "null");
            logger.info("═══════════════════════════════════════════════════════════");

            return result;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - start;

            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("🔄 AROUND ADVICE - Method Execution Failed");
            logger.error("Method: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            logger.error("Execution Time: {} ms", executionTime);
            logger.error("Exception: {}", e.getClass().getName());
            logger.error("═══════════════════════════════════════════════════════════");

            throw e;
        }
    }

    /**
     * Helper method to get request headers
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.put(headerName, request.getHeader(headerName));
        });
        return headers;
    }
}
