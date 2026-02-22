package com.OBE.workflow.camundafeature.camunda;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @GetMapping("/test")
    public Map<String, Object> testAdmin(Authentication authentication) {
        return Map.of(
                "message", "ADMIN ACCESS OK",
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
