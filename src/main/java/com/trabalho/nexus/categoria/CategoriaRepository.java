package com.trabalho.nexus.categoria;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trabalho.nexus.usuario.Usuario;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
	
	List<Categoria> findAllByUsuario(Usuario usuario);
    
    Optional<Categoria> findByIdAndUsuario(Long id, Usuario usuario);
    
    boolean existsByUsuarioAndDescricao(Usuario usuario, String descricao);
}
