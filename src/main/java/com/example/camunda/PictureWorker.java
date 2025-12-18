package com.example.camunda;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class PictureWorker {

    private final static Logger LOG = LoggerFactory.getLogger(PictureWorker.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public PictureWorker(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @JobWorker(type = "fetch-picture")
    public void handleFetchPicture(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String animal = (String) variables.get("animal");

        LOG.info("Fetching picture for: {}", animal);

        String imageUrl = getImageUrl(animal);

        // Save to Redis
        try {
            PictureRecord record = new PictureRecord(animal, imageUrl, LocalDateTime.now().toString());
            String jsonEntry = objectMapper.writeValueAsString(record);
            String redisKey = "picture:" + animal + ":" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(redisKey, jsonEntry);
            LOG.info("Saved picture record to Redis with key: {}", redisKey);
        } catch (Exception e) {
            LOG.error("Failed to save picture record to Redis", e);
        }

        client.newCompleteCommand(job.getKey())
                .variables(Map.of("pictureUrl", imageUrl))
                .send()
                .join();

        LOG.info("Job completed with image URL: {}", imageUrl);
    }

    private String getImageUrl(String animal) {
        if (animal == null || animal.isEmpty())
            return "https://images.unsplash.com/photo-1644365977963-e96e883a8e74?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";

        return switch (animal.toLowerCase()) {
            case "cat" ->
                "https://plus.unsplash.com/premium_photo-1667030474693-6d0632f97029?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D0";
            case "dog" -> "https://place.dog/500/500";
            case "bear" -> "https://placebear.com/500/500";
            default ->
                "https://images.unsplash.com/photo-1644365977963-e96e883a8e74?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
        };
    }
}
