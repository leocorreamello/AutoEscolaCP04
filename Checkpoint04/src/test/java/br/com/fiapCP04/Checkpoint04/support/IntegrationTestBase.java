package br.com.fiapCP04.Checkpoint04.support;

import br.com.fiapCP04.Checkpoint04.domain.Aluno;
import br.com.fiapCP04.Checkpoint04.domain.Endereco;
import br.com.fiapCP04.Checkpoint04.domain.Especialidade;
import br.com.fiapCP04.Checkpoint04.domain.Instrutor;
import br.com.fiapCP04.Checkpoint04.domain.Perfil;
import br.com.fiapCP04.Checkpoint04.domain.Usuario;
import br.com.fiapCP04.Checkpoint04.repository.AlunoRepository;
import br.com.fiapCP04.Checkpoint04.repository.InstrutorRepository;
import br.com.fiapCP04.Checkpoint04.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(IntegrationTestBase.RelogioConfig.class)
public abstract class IntegrationTestBase {

    /** Segunda-feira, 07/01/2030 as 08:00. */
    protected static final LocalDateTime AGORA = LocalDateTime.of(2030, 1, 7, 8, 0);

    private static final AtomicInteger SEQUENCIA = new AtomicInteger();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RelogioMutavel relogio;

    @Autowired
    protected AlunoRepository alunoRepository;

    @Autowired
    protected InstrutorRepository instrutorRepository;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class RelogioConfig {
        @Bean
        @Primary
        RelogioMutavel relogioMutavel() {
            return new RelogioMutavel(ZoneId.of("America/Sao_Paulo"), AGORA);
        }
    }

    @BeforeEach
    void reiniciarRelogio() {
        relogio.definir(AGORA);
    }

    protected String tokenDe(String login, String senha) throws Exception {
        String json = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "%s", "senha": "%s"}
                                """.formatted(login, senha)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(json, "$.token");
    }

    protected String tokenAdmin() throws Exception {
        return tokenDe("admin", "admin123");
    }

    protected Usuario criarUsuario(String login, String senha, Perfil perfil) {
        return usuarioRepository.save(new Usuario(login, passwordEncoder.encode(senha), perfil));
    }

    protected Aluno criarAluno(String nome) {
        int n = SEQUENCIA.incrementAndGet();
        return alunoRepository.save(new Aluno(nome, "aluno" + n + "@email.com", "11999990000", "cpf-" + n, endereco()));
    }

    protected Instrutor criarInstrutor(String nome) {
        int n = SEQUENCIA.incrementAndGet();
        return instrutorRepository.save(new Instrutor(
                nome, "instrutor" + n + "@email.com", "11999990000", "cnh-" + n, Especialidade.CARROS, endereco()));
    }

    protected Endereco endereco() {
        return new Endereco("Rua A", "100", null, "Centro", "Sao Paulo", "SP", "01000-000");
    }
}
