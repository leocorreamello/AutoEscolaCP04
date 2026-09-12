package br.com.fiapCP03.Checkpoint03.service;

import br.com.fiapCP03.Checkpoint03.domain.Perfil;
import br.com.fiapCP03.Checkpoint03.domain.Usuario;
import br.com.fiapCP03.Checkpoint03.dto.AlterarSenhaRequest;
import br.com.fiapCP03.Checkpoint03.dto.UsuarioCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.UsuarioUpdateRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import br.com.fiapCP03.Checkpoint03.repository.UsuarioRepository;
import br.com.fiapCP03.Checkpoint03.vo.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioCreateRequest request) {
        if (repository.existsByLogin(request.login())) {
            throw new RegraNegocioException("Ja existe um usuario com esse login");
        }

        Usuario usuario = new Usuario(
                request.login(),
                passwordEncoder.encode(request.senha()),
                request.perfil()
        );

        return toResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Long id, UsuarioUpdateRequest request, Usuario autenticado) {
        Usuario usuario = buscar(id);

        if (usuario.getId().equals(autenticado.getId()) && request.perfil() != Perfil.ADMIN) {
            throw new RegraNegocioException("Nao e permitido remover o perfil de administrador do proprio usuario");
        }

        usuario.setPerfil(request.perfil());
        return toResponse(usuario);
    }

    @Transactional
    public void excluir(Long id, Usuario autenticado) {
        Usuario usuario = buscar(id);

        if (usuario.getId().equals(autenticado.getId())) {
            throw new RegraNegocioException("Nao e permitido excluir o proprio usuario");
        }

        repository.delete(usuario);
    }

    @Transactional
    public void alterarSenha(Usuario autenticado, AlterarSenhaRequest request) {
        Usuario usuario = buscar(autenticado.getId());

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getLogin(), usuario.getPerfil());
    }
}
