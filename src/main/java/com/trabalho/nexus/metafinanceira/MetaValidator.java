package com.trabalho.nexus.metafinanceira;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;

@Component
public class MetaValidator {

    private final MetaFinanceiraRepository repo;

    // Injeção de dependência via construtor
    public MetaValidator(MetaFinanceiraRepository repo) {
        this.repo = repo;
    }

    public void validarCriacao(MetaFinanceiraRequestDTO dados, Usuario usuario) {
        if(dados.descricao().length() <= 0){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da meta deve ter pelo menos 1 caractere.");
        }
        
        if (repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já tem uma meta com este nome.");
        }

        if (dados.dataInicial().isAfter(dados.dataFinal())) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data inicial deve ser menor ou igual que a data final.");
        }

        if(dados.valorMeta() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da meta deve ser maior que 0.");
        }
    }

    public void validarAtualizacao(MetaFinanceira existente, MetaFinanceiraRequestDTO dados, Usuario usuario) {
        if(dados.descricao().length() <= 0){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da meta deve ter pelo menos 1 caractere.");
        }

        if (!existente.getDescricao().equals(dados.descricao()) && 
            repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já tem uma meta com este nome.");
        }

        if (dados.dataInicial().isAfter(dados.dataFinal())) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data inicial deve ser menor ou igual que a data final.");
        }

        if(dados.valorMeta() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da meta deve ser maior que 0.");
        }
    }
}