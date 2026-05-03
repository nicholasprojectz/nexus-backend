package com.trabalho.nexus.movimentacao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.trabalho.nexus.usuario.Usuario;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findAllByUsuario(Usuario usuario);
    Optional<Movimentacao> findByIdAndUsuario(Long id, Usuario usuario);
}