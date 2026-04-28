package com.trabalho.nexus.usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<TokenResponseDTO> registrar(@RequestBody RegisterRequestDTO data) {
        // Devolve o JSON com { "token": "...", "nome": "..." }
        return ResponseEntity.ok(this.authService.registrar(data));
    }

    @PostMapping("/logar")
    public ResponseEntity<TokenResponseDTO> logar(@RequestBody LoginRequestDTO data) {
        // Devolve o JSON com { "token": "...", "nome": "..." }
        return ResponseEntity.ok(this.authService.logar(data));
    }
}