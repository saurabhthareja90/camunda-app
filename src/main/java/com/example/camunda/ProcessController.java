package com.example.camunda;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProcessController {

    private final static Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/start")
    public Map<String, Object> startProcess(@RequestBody StartProcessRequest request) {
        LOG.info("Starting process for animal: {}", request.getAnimal());

        ProcessInstanceResult event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_FetchPicture")
                .latestVersion()
                .variables(Map.of("animal", request.getAnimal()))
                .withResult()
                .send()
                .join();

        LOG.info("Process completed. Variables: {}", event.getVariablesAsMap());

        return event.getVariablesAsMap();
    }
}
