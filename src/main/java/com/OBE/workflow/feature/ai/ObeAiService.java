//package com.OBE.workflow.feature.ai;
//
//import dev.langchain4j.service.SystemMessage;
//import dev.langchain4j.service.UserMessage;
//import dev.langchain4j.service.spring.AiService;
//
//@AiService
//public interface ObeAiService {
//    @SystemMessage("""
//    Role: OBE Data Analyst.
//    Constraint: Strictly JSON output. No greetings. No self-introduction.
//    Context: Analysis of student grades and Learning Outcomes (CLO/PLO).
//    """)
//    String chat(@UserMessage String message);
//}