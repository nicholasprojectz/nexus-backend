package com.trabalho.nexus.usuario;

import com.trabalho.nexus.categoria.Categoria;
import com.trabalho.nexus.categoria.CategoriaRepository;
import com.trabalho.nexus.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final CategoriaRepository catrepo;
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository repository, PasswordEncoder passwordEncoder, 
                       JwtService jwtService, AuthenticationManager authenticationManager, CategoriaRepository catrepo) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.catrepo = catrepo;
    }

    public TokenResponseDTO registrar(RegisterRequestDTO data) {
        if (this.repository.findByEmail(data.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usuário já existe");
        }

        // Criamos o utilizador e encriptamos a senha antes de guardar
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(data.nome());
        novoUsuario.setEmail(data.email());
        novoUsuario.setSenha(passwordEncoder.encode(data.senha()));

        this.repository.save(novoUsuario);

        Categoria categoriaMeta = new Categoria();
        categoriaMeta.setDescricao("Meta Financeira");
        categoriaMeta.setUsuario(novoUsuario); 
        catrepo.save(categoriaMeta);
        
        String token = jwtService.generateToken(novoUsuario.getEmail(), "ROLE_USER");
        return new TokenResponseDTO(token, novoUsuario.getNome());
    }

    public TokenResponseDTO logar(LoginRequestDTO data) {
        // 1. Buscamos o usuário no banco pelo e-mail
        Usuario usuario = repository.findByEmail(data.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usuário ou senha inválidos"));

        // 2. Comparamos a senha digitada com a hash do banco usando a ferramenta nativa
        boolean senhaCorreta = passwordEncoder.matches(data.senha(), usuario.getSenha());

        // 3. Se a senha não bater, bloqueamos o acesso
        if (!senhaCorreta) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usuário ou senha inválidos"); // Mensagem genérica por segurança
        }

        // 4. Se chegou aqui, está validado. Geramos o token.
        String token = jwtService.generateToken(usuario.getEmail(), "ROLE_USER");
        return new TokenResponseDTO(token, usuario.getNome());
    }
}