package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import br.com.joabe.cepapi.domain.service.LogHistoricoService;
import br.com.joabe.cepapi.dto.LogResumoResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Adapter de entrada para consulta do histórico de logs.
 * <p>
 * Todos os filtros são opcionais — sem parâmetros retorna todo o histórico.
 * <p>
 * Exemplos de uso:
 * <pre>
 *   GET /api/v1/logs
 *   GET /api/v1/logs?cep=01310100
 *   GET /api/v1/logs?status=NOT_FOUND
 *   GET /api/v1/logs?de=2024-01-01&ate=2024-12-31
 *   GET /api/v1/logs?cep=01310100&status=SUCCESS&de=2024-06-01&ate=2024-06-30
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/logs")
public class LogHistoricoController {

    private final LogHistoricoService logHistoricoService;

    public LogHistoricoController(LogHistoricoService logHistoricoService) {
        this.logHistoricoService = logHistoricoService;
    }

    @GetMapping
    public ResponseEntity<List<LogResumoResponse>> listar(
            @RequestParam(required = false) String cep,

            @RequestParam(required = false) ConsultaStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {

        List<LogResumoResponse> resultado = logHistoricoService
                .consultar(cep, status, de, ate)
                .stream()
                .map(LogResumoResponse::from)
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
