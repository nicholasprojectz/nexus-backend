package com.trabalho.nexus.categoria;

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
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService catService;

    public CategoriaController(CategoriaService catService) {
        this.catService = catService;
    }
    
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(this.catService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarId(@PathVariable Long id) { 
        return ResponseEntity.ok(this.catService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody CategoriaRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.catService.criar(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id, 
            @RequestBody CategoriaRequestDTO data) { 
        return ResponseEntity.ok(this.catService.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) { 
        this.catService.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}