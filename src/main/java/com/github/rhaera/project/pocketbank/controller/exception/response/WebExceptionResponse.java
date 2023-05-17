package com.github.rhaera.project.pocketbank.controller.exception.response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@RequiredArgsConstructor
@AllArgsConstructor
public class WebExceptionResponse implements Serializable {

    private final Date timestamp;

    private final String details;

    private String message;

}
