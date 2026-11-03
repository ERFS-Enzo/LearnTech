package com.curso.learntech.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutorizacaoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRecusarConsultaSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERADOR")
    void operadorPodeConsultarCursos() throws Exception {
        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OPERADOR")
    void operadorNaoPodeExcluirCurso() throws Exception {
        mockMvc.perform(delete("/api/cursos/999999999"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorPassaPelaAutorizacaoDeExclusao()
            throws Exception {

        mockMvc.perform(delete("/api/cursos/999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveManterHealthPublico() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }
}