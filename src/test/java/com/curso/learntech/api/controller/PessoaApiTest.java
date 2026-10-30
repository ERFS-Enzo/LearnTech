package com.curso.learntech.api.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PessoaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarPessoaComCpfValido() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Ana Souza", "ANA@Exemplo.com", "52998224725")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana@exemplo.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar400QuandoCpfDaPessoaForInvalido() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Ana Souza", "ana@exemplo.com", "52998224724")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.cpf").exists());
    }

    @Test
    void deveRetornar409QuandoCpfJaCadastrado() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Ana Souza", "ana@exemplo.com", "52998224725")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Outra Ana", "outra@exemplo.com", "52998224725")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastradoIgnorandoMaiusculas() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Ana Souza", "ana@exemplo.com", "52998224725")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Bruno Lima", "ANA@EXEMPLO.COM", "11144477735")))
                .andExpect(status().isConflict());
    }

    @Test
    void devePesquisarPessoasPorNomeComPaginacao() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaJson("Zelia Pesquisavel", "zelia@exemplo.com", "52998224725")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pessoas")
                        .param("nome", "pesquisavel")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.conteudo[0].nome").value("Zelia Pesquisavel"))
                .andExpect(jsonPath("$.totalElementos").value(1));
    }

    @Test
    void deveRetornar400QuandoCpfDoAutorForInvalido() throws Exception {
        String json = """
                {
                  "nomeAutor": "Autor CPF invalido",
                  "cpf": "1489000299198981481"
                }
                """;

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.cpf").exists());
    }

    private String pessoaJson(String nome, String email, String cpf) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "cpf": "%s"
                }
                """.formatted(nome, email, cpf);
    }
}