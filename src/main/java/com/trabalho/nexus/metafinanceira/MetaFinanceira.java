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
	private String descricao;
	
	@Column(nullable = false)
	private Long valor_meta;
	
	@Column(name = "data_inicial", nullable = false)
    private Instant dataInicial;

    @Column(name = "data_final", nullable = false)
    private Instant dataFinal;
	
	@Column(nullable = false, length = 1)
    private Character status = 'A';
	
	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	@ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
	
	public MetaFinanceira() {}

	public MetaFinanceira(String descricao, Long valor_meta, Instant data_inicial, Instant data_final, Usuario usuario) {
		super();
		this.descricao = descricao;
		this.valor_meta = valor_meta;
		this.dataInicial = data_inicial;
		this.dataFinal = data_final;
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getValor_meta() {
		return valor_meta;
	}

	public void setValor_meta(Long valor_meta) {
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
