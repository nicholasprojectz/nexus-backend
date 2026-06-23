package com.trabalho.nexus.metafinanceira;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trabalho.nexus.categoria.Categoria;
import com.trabalho.nexus.categoria.CategoriaRepository;
import com.trabalho.nexus.integracao.BcbIntegrationService;
import com.trabalho.nexus.movimentacao.Movimentacao;
import com.trabalho.nexus.movimentacao.MovimentacaoRepository;
import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.usuario.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class MetaFinanceiraService {
    
    private final MetaFinanceiraRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final MetaValidator metaValidator;
    private final MovimentacaoRepository movimentacaoRepository; 
    private final CategoriaRepository categoriaRepository; 
    private final BcbIntegrationService bcbIntegrationService;
    
    public MetaFinanceiraService(MetaFinanceiraRepository repository, UsuarioRepository usuarioRepository,
    MetaValidator val, MovimentacaoRepository movimentacaoRepository, CategoriaRepository categoriaRepository, BcbIntegrationService bcbIntegrationService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.metaValidator = val;
        this.movimentacaoRepository = movimentacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.bcbIntegrationService = bcbIntegrationService;
    }

    
    public MetaFinanceiraResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira meta = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta não encontrada ou acesso negado."));
                
        return converterParaDTO(meta);
    }
    
    public List<MetaFinanceiraResponseDTO> listarTodas() {
        Usuario usuario = getUsuarioLogado();
        
        List<MetaFinanceira> metas = repository.findAllByUsuario(usuario);
        
        return metas.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional
    public MetaFinanceiraResponseDTO criar(MetaFinanceiraRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();

        metaValidator.validarCriacao(dados, usuario);
        
        MetaFinanceira novaMeta = new MetaFinanceira();
        novaMeta.setDescricao(dados.descricao());
        novaMeta.setValor_meta(dados.valorMeta());
        novaMeta.setData_inicial(dados.dataInicial());
        novaMeta.setData_final(dados.dataFinal());
        novaMeta.setUsuario(usuario);
        novaMeta.setTipoInvestimento(dados.tipoInvestimento());
        novaMeta.setPercentualRendimento(dados.percentualRendimento());

        MetaFinanceira salva = repository.save(novaMeta);
        return converterParaDTO(salva);
    }

   
    @Transactional
    public MetaFinanceiraResponseDTO atualizar(Long id, MetaFinanceiraRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira metaExistente = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Meta não encontrada ou acesso negado."));

        metaValidator.validarAtualizacao(metaExistente, dados, usuario);
        
        metaExistente.setDescricao(dados.descricao());
        metaExistente.setValor_meta(dados.valorMeta());
        metaExistente.setData_inicial(dados.dataInicial());
        metaExistente.setData_final(dados.dataFinal());
        metaExistente.setTipoInvestimento(dados.tipoInvestimento());
        metaExistente.setPercentualRendimento(dados.percentualRendimento());
        
        MetaFinanceira metaAtualizada = repository.save(metaExistente);
        
        return converterParaDTO(metaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira meta = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Meta não encontrada ou acesso negado."));
                
        metaValidator.validarExclusao(meta);
        
        repository.delete(meta);
    }

    private MetaFinanceiraResponseDTO converterParaDTO(MetaFinanceira meta) {
        Double saldoAtual = movimentacaoRepository.calcularSaldoDaMeta(meta.getId(), getUsuarioLogado());
        return new MetaFinanceiraResponseDTO(
            meta.getId(),
            meta.getDescricao(),
            meta.getValor_meta(), 
            saldoAtual,
            meta.getData_inicial(),
            meta.getData_final(),
            meta.getUsuario().getId(),
            meta.getStatus(),
            meta.getPercentualRendimento(),
            meta.getTipoInvestimento()
        );
    }
    
    private void efetuarResgate(MetaFinanceira meta, Usuario usuario, Instant agora) {
        Double saldoResgate = movimentacaoRepository.calcularSaldoDaMeta(meta.getId(), usuario);

        Categoria categoriaMeta = categoriaRepository.findByDescricaoAndUsuario("Meta Financeira", usuario)
                .orElseThrow(() -> new RuntimeException("Categoria do sistema não encontrada"));

        Movimentacao resgate = new Movimentacao();
        resgate.setDescricao("Resgate de Meta: " + meta.getDescricao());
        resgate.setValor(saldoResgate);
        resgate.setTipo(0);
        resgate.setData_mov(agora);
        resgate.setUsuario(usuario);
        resgate.setCategoria(categoriaMeta);
        resgate.setMetaFinanceira(null); 
        
        movimentacaoRepository.save(resgate);

        meta.setStatus('C');
        repository.save(meta);
    }
    
    @Transactional
    public void resgatarMetaManualmente(Long idMeta) {
        Usuario usuario = getUsuarioLogado(); 
        
        MetaFinanceira meta = repository.findByIdAndUsuario(idMeta, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta não encontrada ou acesso negado."));

        if (meta.getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta meta já foi resgatada ou concluída.");
        }

        efetuarResgate(meta, usuario, Instant.now()); 
    }
    
    @Transactional
    public void processarMetasVencidas(Usuario usuario) {
        Instant agora = Instant.now();
        List<MetaFinanceira> metasVencidas = repository.findByUsuarioAndStatusAndDataFinalLessThanEqual(usuario, 'A', agora);

        for (MetaFinanceira meta : metasVencidas) {
            efetuarResgate(meta, usuario, agora); 
        }
    }

    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado."));
    }
    
    @Transactional
    public void processarRendimentos(Usuario usuario) {
        Instant agora = Instant.now();
        List<MetaFinanceira> metasAtivas = repository.findByUsuarioAndStatus(usuario, 'A');

        if (metasAtivas.isEmpty()) return;

        Categoria categoriaMeta = categoriaRepository.findByDescricaoAndUsuario("Meta Financeira", usuario)
                .orElseThrow(() -> new RuntimeException("Categoria do sistema não encontrada"));

        Instant dataMaisAntiga = agora;
        
        for (MetaFinanceira meta : metasAtivas) {
            Instant ref = movimentacaoRepository.buscarDataUltimoRendimento(meta.getId())
                    .orElse(meta.getData_inicial());
            
            if (ref.isBefore(dataMaisAntiga)) {
                dataMaisAntiga = ref;
            }
        }

        LocalDate inicioBusca = LocalDate.ofInstant(dataMaisAntiga, ZoneId.of("America/Sao_Paulo"));
        LocalDate fimBusca = LocalDate.ofInstant(agora, ZoneId.of("America/Sao_Paulo"));
        Map<String, Double> taxasReaisBcb = bcbIntegrationService.buscarTaxasSelicHistoricas(inicioBusca, fimBusca);

        DateTimeFormatter formatadorMesAno = DateTimeFormatter.ofPattern("MM/yyyy");
        double taxaSegurancaFallback = 0.0084; // 0.84% 

        	for (MetaFinanceira meta : metasAtivas) {
            
            if (meta.getTipoInvestimento() == null || !"RENDA_FIXA_CDI".equals(meta.getTipoInvestimento())) {
                continue; 
            }

            Instant dataReferencia = movimentacaoRepository.buscarDataUltimoRendimento(meta.getId())
                    .orElse(meta.getData_inicial());

            long diasPassados = java.time.temporal.ChronoUnit.DAYS.between(dataReferencia, agora);

            while (diasPassados >= 30) {
                dataReferencia = dataReferencia.plus(30, java.time.temporal.ChronoUnit.DAYS);
                diasPassados -= 30;

                Double saldoAtual = movimentacaoRepository.calcularSaldoDaMeta(meta.getId(), usuario);
                if (saldoAtual <= 0) continue; 

                String mesAnoReferencia = LocalDate.ofInstant(dataReferencia, ZoneId.of("America/Sao_Paulo"))
                                                   .format(formatadorMesAno);

                Double taxaBaseBcb = taxasReaisBcb.getOrDefault(mesAnoReferencia, taxaSegurancaFallback);

                Double multiplicadorCdi = meta.getPercentualRendimento() / 100.0;
                Double taxaAplicada = taxaBaseBcb * multiplicadorCdi;

                Double valorRendimento = saldoAtual * taxaAplicada;

                Movimentacao rendimento = new Movimentacao();
                
                rendimento.setDescricao(String.format("Rendimento %s%% CDI", meta.getPercentualRendimento()));
                rendimento.setValor(valorRendimento);
                rendimento.setTipo(1); 
                rendimento.setData_mov(dataReferencia); 
                rendimento.setUsuario(usuario);
                rendimento.setCategoria(categoriaMeta);
                rendimento.setMetaFinanceira(meta);
                rendimento.setIsAutomatico(true);
                
                movimentacaoRepository.save(rendimento);
            }
        }
    }
    
}
