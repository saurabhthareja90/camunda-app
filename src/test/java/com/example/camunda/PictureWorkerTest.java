package com.example.camunda;

import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PictureWorkerTest {

    private PictureWorker pictureWorker;
    private JobClient jobClient;
    private ActivatedJob activatedJob;
    private CompleteJobCommandStep1 completeJobCommandStep1;

    @BeforeEach
    void setUp() {
        pictureWorker = new PictureWorker();
        jobClient = mock(JobClient.class);
        activatedJob = mock(ActivatedJob.class);
        completeJobCommandStep1 = mock(CompleteJobCommandStep1.class);

        // Mock the builder chain
        when(jobClient.newCompleteCommand(anyLong())).thenReturn(completeJobCommandStep1);
        when(completeJobCommandStep1.variables(any(Map.class))).thenReturn(completeJobCommandStep1);
        when(completeJobCommandStep1.send()).thenReturn(mock(ZeebeFuture.class));
    }

    @Test
    void testHandleFetchPictureWithDog() {
        // Given
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "dog"));

        // When
        pictureWorker.handleFetchPicture(jobClient, activatedJob);

        // Then
        ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(completeJobCommandStep1).variables(variablesCaptor.capture());

        Map capturedVariables = variablesCaptor.getValue();
        assertEquals("https://place.dog/500/500", capturedVariables.get("pictureUrl"));
    }

    @Test
    void testHandleFetchPictureWithCat() {
        // Given
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "cat"));

        // When
        pictureWorker.handleFetchPicture(jobClient, activatedJob);

        // Then
        ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(completeJobCommandStep1).variables(variablesCaptor.capture());

        Map capturedVariables = variablesCaptor.getValue();
        assertEquals(
                "https://plus.unsplash.com/premium_photo-1667030474693-6d0632f97029?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D0",
                capturedVariables.get("pictureUrl"));
    }

    @Test
    void testHandleFetchPictureWithUnknown() {
        // Given
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("animal", "unknown"));

        // When
        pictureWorker.handleFetchPicture(jobClient, activatedJob);

        // Then
        ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(completeJobCommandStep1).variables(variablesCaptor.capture());

        Map capturedVariables = variablesCaptor.getValue();
        assertEquals(
                "https://images.unsplash.com/photo-1644365977963-e96e883a8e74?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                capturedVariables.get("pictureUrl"));
    }
}
