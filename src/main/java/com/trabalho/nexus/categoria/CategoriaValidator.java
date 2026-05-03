package com.trabalho.nexus.categoria;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;

@Component
public class CategoriaValidator {

    private final CategoriaRepository repo;

    // Injeção de dependência via construtor
    public CategoriaValidator(CategoriaRepository repo) {
        this.repo = repo;
    }

    public void validarCriacao(CategoriaRequestDTO dados, Usuario usuario) {
        if(dados.descricao().length() <= 0){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da categoria deve ter pelo menos 1 caractere.");
        }
        
        if (repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já tem uma categoria com este nome.");
        }
    }

    public void validarAtualizacao(Categoria existente, CategoriaRequestDTO dados, Usuario usuario) {
        if(dados.descricao().length() <= 0){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da categoria deve ter pelo menos 1 caractere.");
        }

        if (!existente.getDescricao().equals(dados.descricao()) && 
            repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já tem uma categoria com este nome.");
        }
    }
}