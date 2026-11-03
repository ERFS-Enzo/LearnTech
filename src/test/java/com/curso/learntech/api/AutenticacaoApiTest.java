package com.curso.learntech.api;

import org.springframework.security.test.context.support.WithMockUser;
import com.curso.learntech.domain.*;
import com.curso.learntech.repository.ContaAcessoRepository;
import com.curso.learntech.repository.PessoaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacaoApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private ContaAcessoRepository contaRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtDecoder decoder;

    private Long pessoaId;
    private Long contaId;
    private String login;

    @BeforeEach
    void preparar() {
        login = "teste-" + UUID.randomUUID();

        // Este teste confirma as próprias transações.
        // Não adicione @Transactional à classe.
        Pessoa pessoa = pessoaRepository.save(new Pessoa(
                "Pessoa Autenticação",
                login + "@exemplo.com",
                gerarCpf(),
                LocalDate.now()));

        pessoaId = pessoa.getId();

        ContaAcesso conta = contaRepository.save(new ContaAcesso(
                pessoa,
                login,
                encoder.encode("SenhaTeste123!"),
                Set.of(PerfilAcesso.OPERADOR)));

        contaId = conta.getId();
    }

    @AfterEach
    void limpar() {
        if (contaId != null) {
            contaRepository.deleteById(contaId);
        }

        if (pessoaId != null) {
            pessoaRepository.deleteById(pessoaId);
        }
    }

    @Test
    void deveEmitirJwtEPermitirConsulta() throws Exception {
        JsonNode resposta = loginValido();
        String accessToken = resposta.get("accessToken").asText();

        var jwt = decoder.decode(accessToken);

        assertEquals(login, jwt.getSubject());
        assertEquals("learntech", jwt.getClaimAsString("iss"));
        assertTrue(jwt.getClaimAsStringList("perfis")
                .contains("OPERADOR"));

        mockMvc.perform(get("/api/cursos")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deveRecusarSenhaIncorreta() throws Exception {
        mockMvc.perform(post("/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "%s",
                                  "senha": "SenhaErrada123!"
                                }
                                """.formatted(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Credenciais inválidas"));
    }

    @Test
    void deveRecusarTokenAlterado() throws Exception {
        String token = loginValido().get("accessToken").asText();
        String[] partes = token.split("\\.");

        char primeiro = partes[2].charAt(0);
        partes[2] = (primeiro == 'A' ? "B" : "A")
                + partes[2].substring(1);

        String alterado = String.join(".", partes);

        mockMvc.perform(get("/api/cursos")
                        .header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRotacionarERevogarFamiliaAoReutilizarToken()
            throws Exception {

        String primeiroRefresh =
                loginValido().get("refreshToken").asText();

        String corpo = mockMvc.perform(
                        post("/api/autenticacao/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(corpoRefresh(primeiroRefresh)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String segundoRefresh = objectMapper.readTree(corpo)
                .get("refreshToken")
                .asText();

        assertNotEquals(primeiroRefresh, segundoRefresh);

        mockMvc.perform(post("/api/autenticacao/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoRefresh(primeiroRefresh)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/autenticacao/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoRefresh(segundoRefresh)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutDeveImpedirRenovacaoMasManterAccessTokenAteExpirar()
            throws Exception {

        JsonNode tokens = loginValido();

        String refresh = tokens.get("refreshToken").asText();
        String access = tokens.get("accessToken").asText();

        mockMvc.perform(post("/api/autenticacao/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoRefresh(refresh)))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/autenticacao/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoRefresh(refresh)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/cursos")
                        .header("Authorization", "Bearer " + access))
                .andExpect(status().isOk());
    }

    @Test
    void deveBloquearLoginAposCincoFalhas() throws Exception {
        for (int tentativa = 0; tentativa < 5; tentativa++) {
            mockMvc.perform(post("/api/autenticacao/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "login": "%s",
                                  "senha": "SenhaErrada123!"
                                }
                                """.formatted(login)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message")
                            .value("Credenciais inválidas"));
        }

        // Mesmo a senha correta deve ser recusada durante o bloqueio.
        mockMvc.perform(post("/api/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "login": "%s",
                              "senha": "SenhaTeste123!"
                            }
                            """.formatted(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Credenciais inválidas"));
    }

    @Test
    @WithMockUser(roles = "OPERADOR")
    void operadorNaoPodeConsultarAuditoria() throws Exception {
        mockMvc.perform(get("/api/auditoria-seguranca"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorPodeConsultarAuditoriaSemReceberSegredos()
            throws Exception {

        loginValido();

        mockMvc.perform(get("/api/auditoria-seguranca")
                        .param("tipo", "LOGIN_SUCESSO")
                        .param("alvoId", contaId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo[0].tipo")
                        .value("LOGIN_SUCESSO"))
                .andExpect(jsonPath("$.conteudo[0].senha")
                        .doesNotExist())
                .andExpect(jsonPath("$.conteudo[0].senhaHash")
                        .doesNotExist())
                .andExpect(jsonPath("$.conteudo[0].accessToken")
                        .doesNotExist())
                .andExpect(jsonPath("$.conteudo[0].refreshToken")
                        .doesNotExist());
    }

    private String corpoRefresh(String token) {
        return """
            {"refreshToken":"%s"}
            """.formatted(token);
    }

    private JsonNode loginValido() throws Exception {
        String corpo = mockMvc.perform(
                        post("/api/autenticacao/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "login": "%s",
                                          "senha": "SenhaTeste123!"
                                        }
                                        """.formatted(login)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Cache-Control", "no-store"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(corpo);
    }

    private String gerarCpf() {
        String base;

        do {
            base = "1" + String.format(
                    "%08d",
                    java.util.concurrent.ThreadLocalRandom.current()
                            .nextInt(100_000_000));
        } while (base.chars().distinct().count() == 1);

        int primeiro = digito(base, 10);
        int segundo = digito(base + primeiro, 11);
        String cpf = base + primeiro + segundo;

        if (pessoaRepository.existsByCpf(cpf)) {
            return gerarCpf();
        }

        return cpf;
    }

    private int digito(String base, int peso) {
        int soma = 0;

        for (int indice = 0; indice < base.length(); indice++) {
            soma += Character.digit(base.charAt(indice), 10)
                    * (peso - indice);
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}