package com.github.rhaera.project.pocketbank.controller;

import com.github.rhaera.project.pocketbank.model.mapper.ContaMapper;
import com.github.rhaera.project.pocketbank.service.ContaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contas/")
@RequiredArgsConstructor
@Slf4j
public class ContaController {
    private final ContaMapper mapper;
    private final ContaService service;
}
