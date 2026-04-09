package com.trabalho.nexus.metafinanceira;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.usuario.UsuarioRepository;

@Service
public class MetaFinanceiraService {
	
    private final MetaFinanceiraRepository repository;
    private final UsuarioRepository usuarioRepository;
    
    public MetaFinanceiraService(MetaFinanceiraRepository repository, UsuarioRepository usuarioRepository) {
    	this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }


    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // --- 1. CREATE (Criar) ---
    public MetaFinanceiraResponseDTO criar(MetaFinanceiraRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();

        MetaFinanceira novaMeta = new MetaFinanceira();
        novaMeta.setDescricao(dados.descricao());
        novaMeta.setValor_meta(dados.valorMeta());
        novaMeta.setData_inicial(dados.dataInicial());
        novaMeta.setData_final(dados.dataFinal());
        novaMeta.setUsuario(usuario);

        MetaFinanceira salva = repository.save(novaMeta);
        return converterParaDTO(salva);
    }

    public List<MetaFinanceiraResponseDTO> listarTodas() {
        Usuario usuario = getUsuarioLogado();
        
        List<MetaFinanceira> metas = repository.findAllByUsuario(usuario);
        
        return metas.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public MetaFinanceiraResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira meta = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada ou acesso negado."));
                
        return converterParaDTO(meta);
    }

    public MetaFinanceiraResponseDTO atualizar(Long id, MetaFinanceiraRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira metaExistente = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada ou acesso negado."));
        
        metaExistente.setDescricao(dados.descricao());
        metaExistente.setValor_meta(dados.valorMeta());
        metaExistente.setData_inicial(dados.dataInicial());
        metaExistente.setData_final(dados.dataFinal());
        
        MetaFinanceira metaAtualizada = repository.save(metaExistente);
        
        return converterParaDTO(metaAtualizada);
    }

    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        MetaFinanceira meta = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada ou acesso negado."));
                
        repository.delete(meta);
    }

    private MetaFinanceiraResponseDTO converterParaDTO(MetaFinanceira meta) {
        return new MetaFinanceiraResponseDTO(
            meta.getId(), // AQUI: Extraímos o ID real da entidade salva
            meta.getDescricao(),
            meta.getValor_meta(), 
            meta.getData_inicial(),
            meta.getData_final(),
            meta.getUsuario().getId()
        );
    }
}
