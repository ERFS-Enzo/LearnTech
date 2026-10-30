package com.curso.learntech.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devePublicarContratoOpenApiComRotasEssenciais() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("LearnTech API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths['/api/categorias/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/categorias/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/api/cursos/{id}'].get.summary")
                        .value("Consultar curso por ID"))
                .andExpect(jsonPath("$.paths['/api/cursos/{id}'].get.responses['404'].content")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/cursos/{id}'].delete").exists())
                .andExpect(jsonPath("$.paths['/api/cursos/{id}/carga-horaria/acrescimos'].post")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/cursos/{id}/carga-horaria/reducoes'].post")
                        .exists())
                .andExpect(jsonPath("$.components.schemas.CursoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ApiError").exists());
    }

    @Test
    void devePublicarRotasDeIdentidadePapeisEInscricoes() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/pessoas'].post").exists())
                .andExpect(jsonPath("$.paths['/api/alunos'].post").exists())
                .andExpect(jsonPath("$.paths['/api/colaboradores'].post").exists())
                .andExpect(jsonPath("$.paths['/api/cursos/{id}/vagas'].put").exists())
                .andExpect(jsonPath("$.paths['/api/inscricoes'].post").exists())
                .andExpect(jsonPath("$.paths['/api/inscricoes/{id}/finalizacao'].post")
                        .exists())
                .andExpect(jsonPath("$.components.schemas.InscricaoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.PessoaResponse").exists());
    }

}