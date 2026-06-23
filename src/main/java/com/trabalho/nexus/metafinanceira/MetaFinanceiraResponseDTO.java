package com.trabalho.nexus.metafinanceira;

import java.time.Instant;

public record MetaFinanceiraResponseDTO (
	    Long id,
	    String descricao,
	    Double valorMeta, 
	    Double saldo, 
	    Instant dataInicial, 
	    Instant dataFinal, 
	    Long usuarioId,
	    Character status,
	    double percentualRendimento,
	    String tipoInvestimento
	) {}