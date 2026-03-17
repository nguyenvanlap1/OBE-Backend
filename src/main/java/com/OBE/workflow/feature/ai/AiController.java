//package com.OBE.workflow.feature.ai;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/ai")
//public class AiController {
//
//    private final ObeAiService obeAiService;
//
//    public AiController(ObeAiService obeAiService) {
//        this.obeAiService = obeAiService;
//    }
//
//    @GetMapping("/test")
//    public String test(@RequestParam(value = "prompt", defaultValue = "Phân tích điểm CLO1: 8,7,6,9") String prompt) {
//        // Gọi AI với nội dung bạn nhập từ URL
//        return obeAiService.chat(prompt);
//    }
//}
