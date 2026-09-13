package br.com.fiapCP04.Checkpoint04;

import br.com.fiapCP04.Checkpoint04.domain.Aluno;
import br.com.fiapCP04.Checkpoint04.domain.Instrutor;
import br.com.fiapCP04.Checkpoint04.support.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlunoInstrutorIntegrationTest extends IntegrationTestBase {

    private static final String ENDERECO_JSON = """
            {"logradouro": "Rua B", "bairro": "Centro", "cidade": "Sao Paulo", "uf": "SP", "cep": "01000-000"}
            """;

    private String token;

    @BeforeEach
    void autenticar() throws Exception {
        token = tokenAdmin();
    }

    @Test
    void cadastraAlunoSemNumeroEComplemento() throws Exception {
        mockMvc.perform(post("/alunos")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Ana", "email": "ana@email.com", "telefone": "11988887777",
                                 "cpf": "123.456.789-00", "endereco": %s}
                                """.formatted(ENDERECO_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));
    }

    @Test
    void naoCadastraAlunoSemCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/alunos")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Ana", "email": "ana@email.com", "endereco": %s}
                                """.formatted(ENDERECO_JSON)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo", hasItem("cpf")))
                .andExpect(jsonPath("$[*].campo", hasItem("telefone")));
    }

    @Test
    void listagemDeAlunosOrdenadaPorNomeEPaginadaCom10() throws Exception {
        for (int i = 12; i >= 1; i--) {
            criarAluno("Aluno %02d".formatted(i));
        }

        mockMvc.perform(get("/alunos").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].nome").value("Aluno 01"))
                .andExpect(jsonPath("$.content[9].nome").value("Aluno 10"));
    }

    @Test
    void exclusaoDeAlunoApenasInativa() throws Exception {
        Aluno aluno = criarAluno("Carlos");

        mockMvc.perform(delete("/alunos/{id}", aluno.getId()).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        assertThat(alunoRepository.findById(aluno.getId())).get()
                .extracting(Aluno::getAtivo).isEqualTo(false);

        mockMvc.perform(get("/alunos").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.content[*].nome", not(hasItem("Carlos"))));

        mockMvc.perform(delete("/alunos/{id}", aluno.getId()).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizaNomeTelefoneEEnderecoDoAluno() throws Exception {
        Aluno aluno = criarAluno("Carlos");

        mockMvc.perform(put("/alunos/{id}", aluno.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Carlos Silva", "telefone": "11911112222", "endereco": %s}
                                """.formatted(ENDERECO_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Silva"));
    }

    @Test
    void naoPermiteAlterarEmailOuCpfDoAluno() throws Exception {
        Aluno aluno = criarAluno("Carlos");

        mockMvc.perform(put("/alunos/{id}", aluno.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Carlos", "telefone": "11911112222", "endereco": %s,
                                 "email": "novo@email.com", "cpf": "999"}
                                """.formatted(ENDERECO_JSON)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo", hasItem("email")))
                .andExpect(jsonPath("$[*].campo", hasItem("cpf")));
    }

    @Test
    void naoPermiteAlterarEmailCnhOuEspecialidadeDoInstrutor() throws Exception {
        Instrutor instrutor = criarInstrutor("Paulo");

        mockMvc.perform(put("/instrutores/{id}", instrutor.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Paulo", "telefone": "11911112222", "endereco": %s,
                                 "email": "novo@email.com", "cnh": "999", "especialidade": "MOTOS"}
                                """.formatted(ENDERECO_JSON)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo", hasItem("email")))
                .andExpect(jsonPath("$[*].campo", hasItem("cnh")))
                .andExpect(jsonPath("$[*].campo", hasItem("especialidade")));
    }

    @Test
    void exclusaoDeInstrutorApenasInativa() throws Exception {
        Instrutor instrutor = criarInstrutor("Paulo");

        mockMvc.perform(delete("/instrutores/{id}", instrutor.getId()).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        assertThat(instrutorRepository.findById(instrutor.getId())).get()
                .extracting(Instrutor::getAtivo).isEqualTo(false);
    }
}
