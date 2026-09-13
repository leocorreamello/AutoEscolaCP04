package br.com.fiapCP04.Checkpoint04;

import br.com.fiapCP04.Checkpoint04.domain.Aluno;
import br.com.fiapCP04.Checkpoint04.domain.Instrutor;
import br.com.fiapCP04.Checkpoint04.support.IntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InstrucaoIntegrationTest extends IntegrationTestBase {

    /** Terca-feira, 08/01/2030 as 10:00 (26 horas depois de AGORA). */
    private static final LocalDateTime TERCA_10H = LocalDateTime.of(2030, 1, 8, 10, 0);

    private String token;
    private Aluno aluno;
    private Instrutor instrutor;

    @BeforeEach
    void preparar() throws Exception {
        token = tokenAdmin();
        aluno = criarAluno("Aluno");
        instrutor = criarInstrutor("Instrutor");
    }

    @Test
    void agendaInstrucaoComInstrutorInformado() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAluno").value(aluno.getId()))
                .andExpect(jsonPath("$.idInstrutor").value(instrutor.getId()))
                .andExpect(jsonPath("$.dataHora").value("2030-01-08T10:00:00"))
                .andExpect(jsonPath("$.cancelada").value(false));
    }

    @Test
    void semInstrutorInformadoEscolheUmInstrutorDisponivel() throws Exception {
        Instrutor livre = criarInstrutor("Livre");
        Instrutor inativo = criarInstrutor("Inativo");
        inativo.setAtivo(false);
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());

        agendar(criarAluno("Outro").getId(), null, TERCA_10H.plusMinutes(30))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idInstrutor").value(livre.getId()));
    }

    @Test
    void semInstrutorInformadoENenhumDisponivelRetornaErro() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());

        agendar(criarAluno("Outro").getId(), null, TERCA_10H)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Nenhum instrutor disponivel na data/hora informada"));
    }

    @Test
    void naoAgendaNoDomingo() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), LocalDateTime.of(2030, 1, 13, 10, 0))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("horario de funcionamento")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"05:59", "20:01", "21:00", "23:00"})
    void naoAgendaForaDoHorarioDeFuncionamento(String horario) throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H.toLocalDate().atTime(LocalTime.parse(horario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("horario de funcionamento")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"06:00", "20:00"})
    void agendaNosLimitesDoHorarioDeFuncionamento(String horario) throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H.toLocalDate().atTime(LocalTime.parse(horario)))
                .andExpect(status().isCreated());
    }

    @Test
    void agendaNoSabado() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), LocalDateTime.of(2030, 1, 12, 10, 0))
                .andExpect(status().isCreated());
    }

    @Test
    void exigeAntecedenciaMinimaDe30Minutos() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), AGORA.plusMinutes(29))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("30 minutos")));

        agendar(aluno.getId(), instrutor.getId(), AGORA.plusMinutes(30))
                .andExpect(status().isCreated());
    }

    @Test
    void naoAgendaParaDataNoPassado() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), AGORA.minusDays(1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoAgendaComAlunoInativo() throws Exception {
        aluno.setAtivo(false);

        agendar(aluno.getId(), instrutor.getId(), TERCA_10H)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Nao e permitido agendar instrucao para aluno inativo"));
    }

    @Test
    void naoAgendaComInstrutorInativo() throws Exception {
        instrutor.setAtivo(false);

        agendar(aluno.getId(), instrutor.getId(), TERCA_10H)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Nao e permitido agendar instrucao com instrutor inativo"));
    }

    @Test
    void naoAgendaComAlunoOuInstrutorInexistente() throws Exception {
        agendar(999_999L, instrutor.getId(), TERCA_10H).andExpect(status().isBadRequest());
        agendar(aluno.getId(), 999_999L, TERCA_10H).andExpect(status().isBadRequest());
    }

    @Test
    void naoAgendaSemAlunoOuDataHora() throws Exception {
        mockMvc.perform(post("/instrucoes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoPermiteMaisDeDuasInstrucoesNoMesmoDiaParaOAluno() throws Exception {
        Long primeira = idDe(agendar(aluno.getId(), null, TERCA_10H.withHour(9)).andExpect(status().isCreated()));
        agendar(aluno.getId(), null, TERCA_10H.withHour(11)).andExpect(status().isCreated());

        agendar(aluno.getId(), null, TERCA_10H.withHour(14))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("2 instrucoes")));

        cancelar(primeira, "ALUNO_DESISTIU").andExpect(status().isOk());

        agendar(aluno.getId(), null, TERCA_10H.withHour(14)).andExpect(status().isCreated());
    }

    @Test
    void naoPermiteInstrutorComInstrucoesSobrepostas() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());
        Long outroAluno = criarAluno("Outro").getId();

        agendar(outroAluno, instrutor.getId(), TERCA_10H)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("instrutor ja possui")));
        agendar(outroAluno, instrutor.getId(), TERCA_10H.plusMinutes(30)).andExpect(status().isBadRequest());
        agendar(outroAluno, instrutor.getId(), TERCA_10H.minusMinutes(30)).andExpect(status().isBadRequest());

        agendar(outroAluno, instrutor.getId(), TERCA_10H.plusHours(1)).andExpect(status().isCreated());
    }

    @Test
    void instrucaoCanceladaLiberaOHorarioDoInstrutor() throws Exception {
        Long id = idDe(agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated()));
        cancelar(id, "INSTRUTOR_CANCELOU").andExpect(status().isOk());

        agendar(criarAluno("Outro").getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());
    }

    @Test
    void naoPermiteAlunoComInstrucoesSobrepostas() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());

        agendar(aluno.getId(), criarInstrutor("Outro").getId(), TERCA_10H.plusMinutes(30))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("aluno ja possui")));
    }

    @Test
    void cancelaInstrucaoComAntecedenciaDe24Horas() throws Exception {
        Long id = idDe(agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated()));
        relogio.definir(TERCA_10H.minusHours(24));

        cancelar(id, "OUTROS")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelada").value(true))
                .andExpect(jsonPath("$.motivoCancelamento").value("OUTROS"));
    }

    @Test
    void naoCancelaComMenosDe24HorasDeAntecedencia() throws Exception {
        Long id = idDe(agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated()));
        relogio.definir(TERCA_10H.minusHours(24).plusMinutes(1));

        cancelar(id, "ALUNO_DESISTIU")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("24 horas")));
    }

    @Test
    void cancelamentoExigeMotivoValido() throws Exception {
        Long id = idDe(agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated()));

        mockMvc.perform(patch("/instrucoes/{id}/cancelamento", id)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("motivo"));

        cancelar(id, "CHOVEU")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("ALUNO_DESISTIU, INSTRUTOR_CANCELOU, OUTROS")));
    }

    @Test
    void naoCancelaInstrucaoJaCanceladaOuInexistente() throws Exception {
        Long id = idDe(agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated()));
        cancelar(id, "OUTROS").andExpect(status().isOk());

        cancelar(id, "OUTROS")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A instrucao ja foi cancelada"));

        cancelar(999_999L, "OUTROS").andExpect(status().isNotFound());
    }

    @Test
    void listaInstrucoes() throws Exception {
        agendar(aluno.getId(), instrutor.getId(), TERCA_10H).andExpect(status().isCreated());

        mockMvc.perform(get("/instrucoes").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeAluno").value("Aluno"))
                .andExpect(jsonPath("$.content[0].nomeInstrutor").value("Instrutor"));
    }

    private ResultActions agendar(Long idAluno, Long idInstrutor, LocalDateTime dataHora) throws Exception {
        return mockMvc.perform(post("/instrucoes")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"idAluno": %d, "idInstrutor": %s, "dataHora": "%s"}
                        """.formatted(idAluno, idInstrutor, dataHora)));
    }

    private ResultActions cancelar(Long id, String motivo) throws Exception {
        return mockMvc.perform(patch("/instrucoes/{id}/cancelamento", id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"motivo": "%s"}
                        """.formatted(motivo)));
    }

    private Long idDe(ResultActions resultado) throws Exception {
        Number id = JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
