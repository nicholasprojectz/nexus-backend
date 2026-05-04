package com.trabalho.nexus.movimentacao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;

@Component
public class MovimentacoesValidator {

    public void validarCriacao(MovimentacaoRequestDTO dados, Usuario usuario) {

        if(dados.idCategoria() == null && dados.idMeta() == null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma meta financeira ou categoria para realizar a movimentação.");
        }

        if(dados.idCategoria() == null && dados.idMeta() != null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione a categoria de Metas para realizar a movimentação");
        }

        if(dados.valor() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor deve ser maior que 0.");
        }
        if(dados.dataMov() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma data de movimentação.");
        }

        if(dados.tipo() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um tipo de movimentação.");
        }

    }
}