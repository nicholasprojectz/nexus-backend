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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String descricao;
	
	@Column(nullable = false)
	private Long valor_meta;
	

	@Column(nullable = false)
    private Instant data_inicial;

	@Column(nullable = false)
    private Instant data_final;
	
	@ManyToOne
    @JoinColumn(name = "usuario_id") // Nome da coluna no SQLite
    private Usuario usuario;
	
	public MetaFinanceira() {}

	public MetaFinanceira(String descricao, Long valor_meta, Instant data_inicial, Instant data_final, Usuario usuario) {
		super();
		this.descricao = descricao;
		this.valor_meta = valor_meta;
		this.data_inicial = data_inicial;
		this.data_final = data_final;
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
		return data_inicial;
	}

	public void setData_inicial(Instant data_inicial) {
		this.data_inicial = data_inicial;
	}

	public Instant getData_final() {
		return data_final;
	}

	public void setData_final(Instant data_final) {
		this.data_final = data_final;
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
