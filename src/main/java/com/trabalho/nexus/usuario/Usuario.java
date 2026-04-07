package com.trabalho.nexus.usuario;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String nome;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String senha;
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public Long getId() {
		return this.id;
	}
	public String getNome() {
		return this.nome;
	}
	public String getEmail() {
		return this.email;
	}
	public String getSenha() {
		return this.senha;
	}

	public Usuario(String nome, String email, String senha) {
		super();
		this.nome = nome;
		this.email = email;
		this.senha = senha;
	}
	public Usuario() {
		
	}
	
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			// Define uma permissão padrão. Em sistemas complexos, isso viria de uma tabela de Cargos/Roles.
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}

		@Override
		public String getPassword() {
			return this.senha; // O Spring precisa saber de onde puxar a senha
		}

		@Override
		public String getUsername() {
			return this.email; // O Spring precisa saber qual campo é o identificador único (login)
		}

		@Override
		public boolean isAccountNonExpired() {
			return true; // Conta não expira
		}

		@Override
		public boolean isAccountNonLocked() {
			return true; // Conta não está bloqueada
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true; // Senha não expira
		}

		@Override
		public boolean isEnabled() {
			return true; // Conta está ativa
		}
	
}
