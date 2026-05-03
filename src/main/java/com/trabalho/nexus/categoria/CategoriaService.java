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
    private final CategoriaValidator validator; // Injetando o validador

    public CategoriaService(CategoriaRepository repo, UsuarioRepository usuarioRepository, CategoriaValidator validator) {
        this.repo = repo;
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
    }
    
    public CategoriaResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado(); // Busca o usuário apenas na hora da requisição
        
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

        // Repassa o usuário e os dados para a validação
        validator.validarCriacao(dados, usuario);

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

        // Repassa o usuário, a entidade antiga e os dados novos para a validação
        validator.validarAtualizacao(categoriaExistente, dados, usuario);

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
            cat.getUsuario().getId()
        );
    }

    private Usuario getUsuarioLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(emailLogado)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado."));
    }
}