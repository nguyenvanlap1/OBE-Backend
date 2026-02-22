package com.OBE.workflow.camundafeature.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/camunda")
public class CamundaTestController {

    @Autowired
    private RuntimeService runtimeService;

    @PostMapping("/start-process")
    public ResponseEntity<?> startProcess() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        runtimeService.startProcessInstanceByKey(
                "OBE-project-process",
                Map.of("assigneeUser", username)
        );

        return ResponseEntity.ok("Process started for " + username);
    }

}
