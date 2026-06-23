package com.trabalho.nexus.metafinanceira;

import java.time.Instant;

public record MetaFinanceiraRequestDTO (
	    String descricao,
	    Double valorMeta,
	    Instant dataInicial,
	    Instant dataFinal,
	    double percentualRendimento,
	    String tipoInvestimento
	) {}
