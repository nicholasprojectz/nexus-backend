package com.trabalho.nexus.movimentacao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trabalho.nexus.metafinanceira.MetaFinanceira;
import com.trabalho.nexus.usuario.Usuario;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findAllByUsuario(Usuario usuario);
    Optional<Movimentacao> findByIdAndUsuario(Long id, Usuario usuario);
    
    @Query("SELECT m FROM Movimentacao m WHERE m.usuario = :usuario " +
            "AND (:dataInicio IS NULL OR m.data_mov >= :dataInicio) " +
            "AND (:dataFim IS NULL OR m.data_mov <= :dataFim) " +
            "AND (:valorMin IS NULL OR m.valor >= :valorMin) " +
            "AND (:valorMax IS NULL OR m.valor <= :valorMax) " +
            "AND (:idCategoria IS NULL OR m.categoria.id = :idCategoria) " +
            "AND (:idMeta IS NULL OR m.metaFinanceira.id = :idMeta) " +
            "ORDER BY m.data_mov DESC")
     List<Movimentacao> buscarComFiltrosDinamicos(
             @Param("usuario") Usuario usuario,
             @Param("dataInicio") Instant dataInicio,
             @Param("dataFim") Instant dataFim,
             @Param("valorMin") Double valorMin, 
             @Param("valorMax") Double valorMax,
             @Param("idCategoria") Long idCategoria,
             @Param("idMeta") Long idMeta
     );
    
    @Query("SELECT COALESCE(SUM(CASE WHEN m.tipo = 1 THEN m.valor ELSE -m.valor END), 0.0) " +
            "FROM Movimentacao m WHERE m.metaFinanceira.id = :idMeta AND m.usuario = :usuario")
     Double calcularSaldoDaMeta(@Param("idMeta") Long idMeta, @Param("usuario") Usuario usuario);
	boolean existsByMetaFinanceira(MetaFinanceira existente);
}