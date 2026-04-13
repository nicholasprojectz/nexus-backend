package com.trabalho.nexus.metafinanceira;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trabalho.nexus.usuario.Usuario;

public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, Long>{
	List<MetaFinanceira> findAllByUsuario(Usuario usuario);
    
  
    Optional<MetaFinanceira> findByIdAndUsuario(Long id, Usuario usuario);
    
    boolean existsByUsuarioAndDescricao(Usuario usuario, String descricao);
}
