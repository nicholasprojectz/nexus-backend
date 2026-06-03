package com.trabalho.nexus.movimentacao;

import java.time.Instant;
import com.trabalho.nexus.usuario.Usuario;
import com.trabalho.nexus.metafinanceira.MetaFinanceira;
import com.trabalho.nexus.categoria.Categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String descricao;
    
    @Column(nullable = false)
    private Double valor;
    
    @Column(nullable = false)
    private Integer tipo;
    
    @Column(nullable = false)
    private Instant data_mov;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "id_meta")
    private MetaFinanceira metaFinanceira;
    
    @Column(name = "is_automatico")
    private Boolean isAutomatico = false;

    public Boolean getIsAutomatico() {
		return isAutomatico;
	}

	public void setIsAutomatico(Boolean isAutomatico) {
		this.isAutomatico = isAutomatico;
	}

	public Movimentacao() {}

    public Movimentacao(String descricao, Double valor, Integer tipo, Instant data_mov, Usuario usuario, Categoria categoria, MetaFinanceira metaFinanceira, Boolean isAutomatico) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data_mov = data_mov;
        this.usuario = usuario;
        this.categoria = categoria;
        this.metaFinanceira = metaFinanceira;
		this.isAutomatico = isAutomatico;

    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDescricao() { 
        return descricao; 
    }
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public Double getValor() { 
        return valor; 
    }
    public void setValor(Double valor) { 
        this.valor = valor; 
    }

    public Integer getTipo() { 
        return tipo; 
    }
    public void setTipo(Integer tipo) { 
        this.tipo = tipo; 
    }

    public Instant getData_mov() { 
        return data_mov; 
    }
    public void setData_mov(Instant data_mov) { 
        this.data_mov = data_mov; 
    }

    public Usuario getUsuario() { 
        return usuario; 
    }
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }

    public Categoria getCategoria() { 
        return categoria; 
    }
    public void setCategoria(Categoria categoria) { 
        this.categoria = categoria; 
    }

    public MetaFinanceira getMetaFinanceira() { 
        return metaFinanceira; 
    }
    public void setMetaFinanceira(MetaFinanceira metaFinanceira) { 
        this.metaFinanceira = metaFinanceira; 
    }
}