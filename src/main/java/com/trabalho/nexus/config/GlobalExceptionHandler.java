package com.trabalho.nexus.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> lidarComResponseStatusException(
            ResponseStatusException ex, 
            HttpServletRequest request) { // Injetamos a requisição para pegar o path
        
        // Usamos LinkedHashMap para garantir que a ordem dos campos no JSON seja mantida
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("timestamp", Instant.now().toString());
        resposta.put("status", ex.getStatusCode().value());
        resposta.put("erro", ex.getReason()); // Aqui vai a sua mensagem customizada
        resposta.put("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode()).body(resposta);
    }
}