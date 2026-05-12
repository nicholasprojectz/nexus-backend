package com.trabalho.nexus.metafinanceira;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trabalho.nexus.categoria.Categoria;
import com.trabalho.nexus.categoria.CategoriaRepository;
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

    
    public MetaFinanceiraService(MetaFinanceiraRepository repository, UsuarioRepository usuarioRepository,
    MetaValidator val, MovimentacaoRepository movimentacaoRepository, CategoriaRepository categoriaRepository) {
    	this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.metaValidator = val;
        this.movimentacaoRepository = movimentacaoRepository;
        this.categoriaRepository = categoriaRepository;
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

        MetaFinanceira novaMeta = new MetaFinanceira();
        novaMeta.setDescricao(dados.descricao());
        novaMeta.setValor_meta(dados.valorMeta());
        novaMeta.setData_inicial(dados.dataInicial());
        novaMeta.setData_final(dados.dataFinal());
        novaMeta.setUsuario(usuario);

        metaValidator.validarCriacao(dados, usuario);
        
        MetaFinanceira salva = repository.save(novaMeta);
        return converterParaDTO(salva);
    }

   
    @Transactional
    public MetaFinanceiraResponseDTO atualizar(Long id, MetaFinanceiraRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira metaExistente = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Meta não encontrada ou acesso negado."));

        metaValidator.validarAtualizacao(metaExistente,dados,usuario);
        
        
        metaExistente.setDescricao(dados.descricao());
        metaExistente.setValor_meta(dados.valorMeta());
        metaExistente.setData_inicial(dados.dataInicial());
        metaExistente.setData_final(dados.dataFinal());
        
        MetaFinanceira metaAtualizada = repository.save(metaExistente);
        
        return converterParaDTO(metaAtualizada);
    }
    @Transactional
    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira meta = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Meta não encontrada ou acesso negado."));
                
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
            meta.getUsuario().getId()
        );
    }
    
    private void efetuarResgate(MetaFinanceira meta, Usuario usuario, Instant agora) {
        // Calcula o saldo
        Double saldoResgate = movimentacaoRepository.calcularSaldoDaMeta(meta.getId(), usuario);

        // Busca a categoria padrão
        Categoria categoriaMeta = categoriaRepository.findByDescricaoAndUsuario("Meta Financeira", usuario)
                .orElseThrow(() -> new RuntimeException("Categoria do sistema não encontrada"));

        // Cria a movimentação de entrada
        Movimentacao resgate = new Movimentacao();
        resgate.setDescricao("Resgate de Meta: " + meta.getDescricao());
        resgate.setValor(saldoResgate);
        resgate.setTipo(0); // Entrada
        resgate.setData_mov(agora);
        resgate.setUsuario(usuario);
        resgate.setCategoria(categoriaMeta);
        resgate.setMetaFinanceira(null); 
        
        movimentacaoRepository.save(resgate);

        // Conclui a meta
        meta.setStatus('C');
        repository.save(meta);
    }
    
    @Transactional
    public void resgatarMetaManualmente(Long idMeta) {
        Usuario usuario = getUsuarioLogado(); // Seu método que pega o usuário do SecurityContext
        
        MetaFinanceira meta = repository.findByIdAndUsuario(idMeta, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta não encontrada ou acesso negado."));

        if (meta.getStatus() == 'C') {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta meta já foi resgatada ou concluída.");
        }

        efetuarResgate(meta, usuario, Instant.now()); // Chama o motor privado
    }
    
    @Transactional
    public void processarMetasVencidas(Usuario usuario) {
    	Instant agora = Instant.now();
        List<MetaFinanceira> metasVencidas = repository.findByUsuarioAndStatusAndDataFinalLessThanEqual(usuario, 'A', agora);

        for (MetaFinanceira meta : metasVencidas) {
            efetuarResgate(meta, usuario, agora); // Chama o motor privado
        }
    }

    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado."));
    }
}
