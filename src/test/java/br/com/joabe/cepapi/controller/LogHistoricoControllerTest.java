package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import br.com.joabe.cepapi.domain.service.LogHistoricoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {LogHistoricoController.class, GlobalExceptionHandler.class})
@DisplayName("LogHistoricoController — testes de contrato HTTP")
class LogHistoricoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogHistoricoService logHistoricoService;

    @Test
    @DisplayName("GET /api/v1/logs sem filtros deve retornar 200 com lista")
    void deveRetornarListaCompleta() throws Exception {
        CepLog log = CepLog.sucesso("01310100", "Av. Paulista", "",
                "Bela Vista", "São Paulo", "SP", "3550308");
        log.setId(1L);

        when(logHistoricoService.consultar(any(), any(), any(), any()))
                .thenReturn(List.of(log));

        mockMvc.perform(get("/api/v1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cep").value("01310100"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].consultedAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/logs?status=NOT_FOUND deve filtrar por status")
    void deveAceitarFiltroStatus() throws Exception {
        when(logHistoricoService.consultar(isNull(), eq(ConsultaStatus.NOT_FOUND), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/logs").param("status", "NOT_FOUND"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/logs?cep=01310100 deve filtrar por CEP")
    void deveAceitarFiltroCep() throws Exception {
        when(logHistoricoService.consultar(eq("01310100"), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/logs").param("cep", "01310100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/logs?de=2024-01-01&ate=2024-12-31 deve aceitar filtro de datas")
    void deveAceitarFiltroDatas() throws Exception {
        when(logHistoricoService.consultar(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/logs")
                        .param("de", "2024-01-01")
                        .param("ate", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/logs com lista vazia deve retornar 200 com array vazio")
    void deveRetornarArrayVazioQuandoSemRegistros() throws Exception {
        when(logHistoricoService.consultar(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
