package com.trabalho.nexus.metafinanceira;

import java.time.Instant;

import com.trabalho.nexus.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "meta_financeira")
public class MetaFinanceira {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String tipoInvestimento;	
	
	@Column(nullable = false)
	private String descricao;
	
	@Column(nullable = false)
    private Double valor_meta;
	
	@Column(name = "data_inicial", nullable = false)
    private Instant dataInicial;

    @Column(name = "data_final", nullable = false)
    private Instant dataFinal;
	
	@Column(nullable = false, length = 1)
    private Character status = 'A';
	
	@ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
	
	private double percentualRendimento;
	
	public MetaFinanceira() {}
	
	public MetaFinanceira(Long id, double percentualRendimento, String tipoInvestimento, String descricao,
			Double valor_meta, Instant dataInicial, Instant dataFinal, Character status, Usuario usuario) {
		super();
		this.id = id;
		this.percentualRendimento = percentualRendimento;
		this.tipoInvestimento = tipoInvestimento;
		this.descricao = descricao;
		this.valor_meta = valor_meta;
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.status = status;
		this.usuario = usuario;
	}

	public double getPercentualRendimento() {
		return percentualRendimento;
	}

	public void setPercentualRendimento(double percentualRendimento) {
		this.percentualRendimento = percentualRendimento;
	}

	public String getTipoInvestimento() {
		return tipoInvestimento;
	}

	public void setTipoInvestimento(String tipoInvestimento) {
		this.tipoInvestimento = tipoInvestimento;
	}


	public Instant getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Instant dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Instant getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Instant dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getValor_meta() {
		return valor_meta;
	}

	public void setValor_meta(Double valor_meta) {
		this.valor_meta = valor_meta;
	}

	public Instant getData_inicial() {
		return dataInicial;
	}

	public void setData_inicial(Instant data_inicial) {
		this.dataInicial = data_inicial;
	}

	public Instant getData_final() {
		return dataFinal;
	}

	public void setData_final(Instant data_final) {
		this.dataFinal = data_final;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
