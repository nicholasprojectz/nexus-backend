package com.trabalho.nexus.metafinanceira;

import java.time.Instant;

public record MetaFinanceiraRequestDTO (
	    
	    String descricao,
	 
	    Long valorMeta, 

	    Instant dataInicial, 

	    Instant dataFinal
	) {}
