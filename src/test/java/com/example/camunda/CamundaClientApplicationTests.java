package com.example.camunda;

import io.camunda.zeebe.client.ZeebeClient;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class CamundaClientApplicationTests {

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient zeebeClient;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

}
