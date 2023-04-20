package com.github.rhaera.project.pocketbank.config.doc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swagger/api/docs")
@OpenAPIDefinition(info =
@Info(
        title   = "Pocket Bank API",
        version = "1.0.0",
        description = "API Agency DB Documentation"
))
public class ApiDocConfig {

}
