package com.trabalho.nexus.metafinanceira;

import java.time.Instant;

public record MetaFinanceiraResponseDTO (
	    Long id, // OBRIGATÓRIO: O frontend precisa disso para editar ou deletar no futuro
	    String descricao,
	    Long valorMeta, 
	    Instant dataInicial, 
	    Instant dataFinal, 
	    Long usuarioId 
	) {}