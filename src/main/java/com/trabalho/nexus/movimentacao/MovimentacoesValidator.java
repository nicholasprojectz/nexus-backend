package com.trabalho.nexus.movimentacao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.metafinanceira.MetaFinanceira;
import com.trabalho.nexus.metafinanceira.MetaFinanceiraRepository;

@Component
public class MovimentacoesValidator {

    private final MetaFinanceiraRepository metaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public MovimentacoesValidator(MetaFinanceiraRepository metaRepository, MovimentacaoRepository movimentacaoRepository) {
        this.metaRepository = metaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

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

        if (dados.idMeta() != null) {
            MetaFinanceira meta = metaRepository.findByIdAndUsuario(dados.idMeta(), usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta financeira não encontrada."));

            if (meta.getStatus() == 'C') {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível vincular movimentações a uma meta concluída.");
            }

            if (dados.tipo() == 0) {
                Double saldoAtual = movimentacaoRepository.calcularSaldoDaMeta(meta.getId(), usuario);
                if (saldoAtual - dados.valor() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente na meta para realizar este saque.");
                }
            }
        }
    }

    public void validarAtualizacao(Movimentacao existente, MovimentacaoRequestDTO dados, Usuario usuario) {
        if (existente.getMetaFinanceira() != null && existente.getMetaFinanceira().getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível alterar uma movimentação de uma meta concluída.");
        }
        if (existente.getIsAutomatico()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é possível alterar lançamentos automáticos do sistema (Rendimentos).");
        }

        validarCriacao(dados, usuario);
    }

    public void validarExclusao(Movimentacao existente) {
    	if (existente.getIsAutomatico()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é possível excluir lançamentos automáticos do sistema (Rendimentos).");
        }
        if (existente.getMetaFinanceira() != null && existente.getMetaFinanceira().getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir uma movimentação de uma meta concluída.");
        }
    }
}