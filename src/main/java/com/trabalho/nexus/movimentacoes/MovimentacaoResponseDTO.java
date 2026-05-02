package com.trabalho.nexus.movimentacao;

import java.time.Instant;

public record MovimentacaoResponseDTO(
    Long id,
    String descricao,
    Double valor,
    Integer tipo,
    Instant dataMov,
    Long idUsuario,
    Long idCategoria,
    Long idMeta
) {}