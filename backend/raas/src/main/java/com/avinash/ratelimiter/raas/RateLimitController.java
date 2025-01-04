package com.avinash.ratelimiter.raas;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/limits")
@CrossOrigin(origins = "*")
public class RateLimitController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${rate.limiter.max-requests}")
    private int maxRequests;

    @Value("${rate.limiter.time-window}")
    private int timeWindow;

    @GetMapping
    public ResponseEntity<Map<String, Object>> rateLimitedEndpoint(
            @RequestHeader("X-Request-Path") String urlPath,
            @RequestHeader("X-User-ID") String userId) {

        String key = userId + ":" + urlPath;
        Long currentCount = incrementRequestCount(key);

        // Calculate remaining quota
        long remainingQuota = Math.max(0, maxRequests - currentCount);

        if (currentCount > maxRequests) {
            return ResponseEntity.status(429)
                    .body(createResponse(userId, urlPath, remainingQuota));
        }

        return ResponseEntity.ok(createResponse(userId, urlPath, remainingQuota));
    }

    private Long incrementRequestCount(String key) {
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(timeWindow));
        }

        return count;
    }

    private Map<String, Object> createResponse(String userId, String requestPath, long remainingQuota) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("requestPath", requestPath);
        response.put("remainingQuota", remainingQuota);
        return response;
    }
}
