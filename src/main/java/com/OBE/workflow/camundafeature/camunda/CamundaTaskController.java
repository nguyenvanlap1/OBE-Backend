package com.OBE.workflow.camundafeature.camunda;

import org.camunda.bpm.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/camunda")
public class CamundaTaskController {

    @Autowired
    private TaskService taskService;

    // 1️⃣ CHỈ lấy task của user đang đăng nhập
    @GetMapping("/tasks")
    public ResponseEntity<?> getMyTasks() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        var tasks = taskService.createTaskQuery()
                .taskAssignee(username)
                .list();

        return ResponseEntity.ok(
                tasks.stream().map(t -> Map.of(
                        "taskId", t.getId(),
                        "taskName", t.getName(),
                        "assignee", t.getAssignee()
                )).toList()
        );
    }

    // 2️⃣ Chỉ complete task nếu task thuộc user đó
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> formData
    ) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        var task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(username)
                .singleResult();

        if (task == null) {
            return ResponseEntity
                    .status(403)
                    .body("Task không thuộc về bạn");
        }

        taskService.complete(taskId, formData);
        return ResponseEntity.ok("Task completed");
    }
}
