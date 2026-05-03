package com.trabalho.nexus.movimentacao;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.usuario.UsuarioRepository;
import com.trabalho.nexus.metafinanceira.MetaFinanceiraRepository;
import com.trabalho.nexus.categoria.CategoriaRepository;

import jakarta.transaction.Transactional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final MetaFinanceiraRepository metaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimentacoesValidator movValidator;

    public MovimentacaoService(MovimentacaoRepository repository, UsuarioRepository usuarioRepository, MetaFinanceiraRepository metaRepository, 
    CategoriaRepository categoriaRepository, MovimentacoesValidator val) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.metaRepository = metaRepository;
        this.categoriaRepository = categoriaRepository;
        this.movValidator = val;
    }

    public MovimentacaoResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        Movimentacao mov = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou acesso negado."));
                
        return converterParaDTO(mov);
    }

    public List<MovimentacaoResponseDTO> listarTodas() {
        Usuario usuario = getUsuarioLogado();
        
        List<Movimentacao> movimentacoes = repository.findAllByUsuario(usuario);
        
        return movimentacoes.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional
    public MovimentacaoResponseDTO criar(MovimentacaoRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();

        movValidator.validarCriacao(dados, usuario);

        Movimentacao novaMov = new Movimentacao();
        novaMov.setDescricao(dados.descricao());
        novaMov.setValor(dados.valor());
        novaMov.setTipo(dados.tipo());
        novaMov.setData_mov(dados.dataMov());
        novaMov.setUsuario(usuario);
        
        novaMov.setCategoria(categoriaRepository.findByIdAndUsuario(dados.idCategoria(), usuario)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou acesso negado.")));

        if (dados.idMeta() != null) {
            novaMov.setMetaFinanceira(metaRepository.findByIdAndUsuario(dados.idMeta(), usuario)
                    .orElseThrow(() -> new RuntimeException("Meta financeira não encontrada ou acesso negado.")));
        }

        return converterParaDTO(repository.save(novaMov));
    }

    @Transactional
    public MovimentacaoResponseDTO atualizar(Long id, MovimentacaoRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();
        
        Movimentacao movExistente = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou acesso negado."));

        movValidator.validarCriacao(dados, usuario);
        
        movExistente.setDescricao(dados.descricao());
        movExistente.setValor(dados.valor());
        movExistente.setTipo(dados.tipo());
        movExistente.setData_mov(dados.dataMov());
        
        movExistente.setCategoria(categoriaRepository.findByIdAndUsuario(dados.idCategoria(), usuario)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou acesso negado.")));

        if (dados.idMeta() != null) {
            movExistente.setMetaFinanceira(metaRepository.findByIdAndUsuario(dados.idMeta(), usuario)
                    .orElseThrow(() -> new RuntimeException("Meta financeira não encontrada ou acesso negado.")));
        } else {
            movExistente.setMetaFinanceira(null);
        }

        return converterParaDTO(repository.save(movExistente));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        Movimentacao mov = repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou acesso negado."));
                
        repository.delete(mov);
    }

    private MovimentacaoResponseDTO converterParaDTO(Movimentacao m) {
        return new MovimentacaoResponseDTO(
            m.getId(),
            m.getDescricao(),
            m.getValor(),
            m.getTipo(),
            m.getData_mov(),
            m.getUsuario().getId(),
            m.getCategoria() != null ? m.getCategoria().getId() : null,
            m.getMetaFinanceira() != null ? m.getMetaFinanceira().getId() : null
        );
    }

    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}