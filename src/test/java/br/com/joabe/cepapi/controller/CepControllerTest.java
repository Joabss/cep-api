package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.exception.CepNotFoundException;
import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.service.CepService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de contrato do adapter de entrada.
 * Verifica status HTTP, JSON de resposta e validações.
 */
@WebMvcTest(controllers = {CepController.class, GlobalExceptionHandler.class})
@DisplayName("CepController — testes de contrato HTTP")
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CepService cepService;

    @Test
    @DisplayName("GET /api/v1/cep/{cep} deve retornar 200 com dados do CEP")
    void deveRetornar200ComDadosCep() throws Exception {
        CepData data = new CepData(
                "01310-100", "Avenida Paulista", "",
                "Bela Vista", "São Paulo", "SP", "3550308"
        );
        when(cepService.consultar(eq("01310100"))).thenReturn(data);

        mockMvc.perform(get("/api/v1/cep/01310100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01310-100"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    @DisplayName("GET /api/v1/cep/{cep} deve retornar 404 quando CEP não encontrado")
    void deveRetornar404QuandoCepNaoEncontrado() throws Exception {
        when(cepService.consultar(eq("99999999")))
                .thenThrow(new CepNotFoundException("99999999"));

        mockMvc.perform(get("/api/v1/cep/99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/cep/{cep} deve retornar 400 para CEP inválido")
    void deveRetornar400ParaCepInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/cep/INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/health deve retornar 200")
    void deveRetornarHealthOk() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
