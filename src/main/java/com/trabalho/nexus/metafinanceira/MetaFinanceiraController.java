package com.trabalho.nexus.metafinanceira;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metas")
public class MetaFinanceiraController {

    private final MetaFinanceiraService metaService;

    public MetaFinanceiraController(MetaFinanceiraService metaService) {
        this.metaService = metaService;
    }

    @PostMapping
    public ResponseEntity<MetaFinanceiraResponseDTO> criar(@RequestBody MetaFinanceiraRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.metaService.criar(data));
    }

    @GetMapping
    public ResponseEntity<List<MetaFinanceiraResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(this.metaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaFinanceiraResponseDTO> buscarId(@PathVariable Long id) { 
        return ResponseEntity.ok(this.metaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaFinanceiraResponseDTO> atualizar(
            @PathVariable Long id, 
            @RequestBody MetaFinanceiraRequestDTO data) { 
        return ResponseEntity.ok(this.metaService.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) { 
        this.metaService.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}