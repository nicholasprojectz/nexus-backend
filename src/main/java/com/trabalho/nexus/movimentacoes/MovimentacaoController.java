package com.trabalho.nexus.movimentacao;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> criar(@RequestBody MovimentacaoRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.criar(data));
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(this.service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> atualizar(@PathVariable Long id, @RequestBody MovimentacaoRequestDTO data) {
        return ResponseEntity.ok(this.service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.service.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204
    }
}