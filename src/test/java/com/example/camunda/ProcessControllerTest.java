package com.example.camunda;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceWithResultCommandStep1;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ZeebeClient zeebeClient;

    @Test
    void testStartProcess() throws Exception {
        // Mock the Zeebe Client chain
        CreateProcessInstanceCommandStep1 step1 = mock(CreateProcessInstanceCommandStep1.class);
        CreateProcessInstanceCommandStep2 step2 = mock(CreateProcessInstanceCommandStep2.class);
        CreateProcessInstanceCommandStep3 step3 = mock(CreateProcessInstanceCommandStep3.class);
        CreateProcessInstanceWithResultCommandStep1 stepResult = mock(
                CreateProcessInstanceWithResultCommandStep1.class);
        ZeebeFuture<ProcessInstanceResult> future = mock(ZeebeFuture.class);
        ProcessInstanceResult result = mock(ProcessInstanceResult.class);

        when(zeebeClient.newCreateInstanceCommand()).thenReturn(step1);
        when(step1.bpmnProcessId(anyString())).thenReturn(step2);
        when(step2.latestVersion()).thenReturn(step3);
        when(step3.variables(any(Map.class))).thenReturn(step3);
        when(step3.withResult()).thenReturn(stepResult);
        when(stepResult.send()).thenReturn(future);
        when(future.join()).thenReturn(result);

        // Mock result
        when(result.getVariablesAsMap()).thenReturn(Map.of("pictureUrl", "http://example.com/dog.jpg"));

        // Execute Request
        mockMvc.perform(post("/api/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"animal\": \"dog\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pictureUrl").value("http://example.com/dog.jpg"));
    }
}
