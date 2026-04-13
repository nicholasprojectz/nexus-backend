package com.trabalho.nexus.categoria;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.usuario.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service	
public class CategoriaService {

    private final CategoriaRepository repo;
    private final UsuarioRepository usuarioRepository;

    public CategoriaService(CategoriaRepository repo, UsuarioRepository usuarioRepository) {
        this.repo = repo;
        this.usuarioRepository = usuarioRepository;
    }
    

    public CategoriaResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        Categoria cat = repo.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada ou acesso negado."));
        
        return converterParaDTO(cat);		
    }
    
    public List<CategoriaResponseDTO> listarTodas() {
        Usuario usuario = getUsuarioLogado();
        
        List<Categoria> categorias = repo.findAllByUsuario(usuario);
        
        return categorias.stream()
                .map(this::converterParaDTO)
                .toList();		
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();

        if (repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria já existente.");
        }

        Categoria novaCategoria = new Categoria();
        novaCategoria.setDescricao(dados.descricao());
        novaCategoria.setUsuario(usuario);
        
        Categoria salva = repo.save(novaCategoria);
        
        return converterParaDTO(salva);
    }
    
    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dados) {
        Usuario usuario = getUsuarioLogado();
        
        Categoria categoriaExistente = repo.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada ou acesso negado."));
        
        if (!categoriaExistente.getDescricao().equals(dados.descricao()) && 
            repo.existsByUsuarioAndDescricao(usuario, dados.descricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já tem uma categoria com este nome.");
        }

        categoriaExistente.setDescricao(dados.descricao());
        
        Categoria atualizada = repo.save(categoriaExistente);
        
        return converterParaDTO(atualizada);
    }
    
    @Transactional
    public void deletar(Long id) {
        Usuario usuario = getUsuarioLogado();
        
        Categoria categoria = repo.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada ou acesso negado."));
        
        repo.delete(categoria);
    }
    

    private CategoriaResponseDTO converterParaDTO(Categoria cat) {
        return new CategoriaResponseDTO(
            cat.getId(),
            cat.getDescricao(),
            cat.getUsuario()
        );
    }

    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}
