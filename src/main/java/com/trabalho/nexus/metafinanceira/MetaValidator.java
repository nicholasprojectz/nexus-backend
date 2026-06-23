package com.trabalho.nexus.metafinanceira;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.movimentacao.MovimentacaoRepository;

@Component
public class MetaValidator {

    private final MetaFinanceiraRepository repo;
    private final MovimentacaoRepository movimentacaoRepository;

    public MetaValidator(MetaFinanceiraRepository repo, MovimentacaoRepository movimentacaoRepository) {
        this.repo = repo;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public void validarCriacao(MetaFinanceiraRequestDTO dados, Usuario usuario) {
    	
    	if ("RENDA_FIXA_CDI".equals(dados.tipoInvestimento())) {
            if ( dados.percentualRendimento() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para investimentos em CDI, informe um percentual válido (ex: 100).");
            }
        } else {
            if (dados.percentualRendimento() > 0) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metas sem investimento não devem possuir percentual de rendimento.");
            }
        }
        if(dados.descricao().trim().isEmpty()){
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
    	if ("RENDA_FIXA_CDI".equals(dados.tipoInvestimento())) {
            if (dados.percentualRendimento() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para investimentos em CDI, informe um percentual válido (ex: 100).");
            }
        } else {
            if ( dados.percentualRendimento() > 0) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metas sem investimento não devem possuir percentual de rendimento.");
            }
        }
        if (existente.getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível alterar uma meta concluída.");
        }

        if(dados.descricao().trim().isEmpty()){
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

    public void validarExclusao(MetaFinanceira existente) {
        // REGRA: Impede a exclusão de uma meta concluída/resgatada
        if (existente.getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível deletar uma meta concluída.");
        }

        boolean possuiMovimentacoes = movimentacaoRepository.existsByMetaFinanceira(existente);
        if (possuiMovimentacoes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível deletar uma meta que possui movimentações bancárias.");
        }
    }
}