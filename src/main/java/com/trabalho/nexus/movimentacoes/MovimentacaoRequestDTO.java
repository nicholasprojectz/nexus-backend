package com.trabalho.nexus.movimentacao;

import java.time.Instant;

public record MovimentacaoRequestDTO(
    String descricao,
    Double valor,
    Integer tipo,
    Instant dataMov,
    Long idCategoria,
    Long idMeta
) {}