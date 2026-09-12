package br.com.fiapCP03.Checkpoint03;

import br.com.fiapCP03.Checkpoint03.domain.Perfil;
import br.com.fiapCP03.Checkpoint03.domain.Usuario;
import br.com.fiapCP03.Checkpoint03.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SegurancaIntegrationTest extends IntegrationTestBase {

    @Test
    void loginComCredenciaisValidasRetornaToken() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void loginComSenhaIncorretaRetorna401() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "errada"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/alunos")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/instrucoes")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoComTokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/alunos").header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCadastraUsuarioComSenhaCriptografada() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "maria", "senha": "senha123", "perfil": "USER"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("maria"))
                .andExpect(jsonPath("$.perfil").value("USER"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        Usuario salvo = usuarioRepository.findByLogin("maria").orElseThrow();
        assertThat(salvo.getSenha()).isNotEqualTo("senha123").startsWith("$2");
        assertThat(passwordEncoder.matches("senha123", salvo.getSenha())).isTrue();

        tokenDe("maria", "senha123");
    }

    @Test
    void naoPermiteCadastrarLoginDuplicado() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "admin", "senha": "senha123", "perfil": "USER"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminListaAtualizaPerfilEExcluiUsuarios() throws Exception {
        String token = tokenAdmin();
        Usuario joao = criarUsuario("joao", "senha123", Perfil.USER);

        mockMvc.perform(get("/usuarios").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].login").value("admin"))
                .andExpect(jsonPath("$.content[1].login").value("joao"));

        mockMvc.perform(put("/usuarios/{id}", joao.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"perfil": "ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ADMIN"));

        mockMvc.perform(delete("/usuarios/{id}", joao.getId()).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        assertThat(usuarioRepository.existsById(joao.getId())).isFalse();
    }

    @Test
    void adminNaoPodeExcluirNemRebaixarASiMesmo() throws Exception {
        String token = tokenAdmin();
        Long idAdmin = usuarioRepository.findByLogin("admin").orElseThrow().getId();

        mockMvc.perform(delete("/usuarios/{id}", idAdmin).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/usuarios/{id}", idAdmin)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"perfil": "USER"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void usuarioComumNaoAcessaGestaoDeUsuarios() throws Exception {
        Usuario joao = criarUsuario("joao", "senha123", Perfil.USER);
        String token = tokenDe("joao", "senha123");

        mockMvc.perform(get("/usuarios").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "novo", "senha": "senha123", "perfil": "ADMIN"}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/usuarios/{id}", joao.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"perfil": "ADMIN"}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/usuarios/{id}", joao.getId()).header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/alunos").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioAlteraAPropriaSenha() throws Exception {
        criarUsuario("joao", "senha123", Perfil.USER);
        String token = tokenDe("joao", "senha123");

        mockMvc.perform(patch("/usuarios/me/senha")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senhaAtual": "senha123", "novaSenha": "novaSenha456"}
                                """))
                .andExpect(status().isNoContent());

        tokenDe("joao", "novaSenha456");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "joao", "senha": "senha123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoAlteraSenhaComSenhaAtualIncorreta() throws Exception {
        criarUsuario("joao", "senha123", Perfil.USER);

        mockMvc.perform(patch("/usuarios/me/senha")
                        .header(HttpHeaders.AUTHORIZATION, tokenDe("joao", "senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"senhaAtual": "errada", "novaSenha": "novaSenha456"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Senha atual incorreta"));
    }
}
