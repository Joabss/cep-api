package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.service.CepService;
import br.com.joabe.cepapi.dto.CepResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Adapter de entrada para consulta de CEP.
 * <p>
 * Valida o formato do CEP usando @Pattern, garantindo que seja 8 dígitos (com ou sem hífen).
 * Exemplo de uso:
 * <pre>
 *   GET /api/v1/cep/01310100
 *   GET /api/v1/cep/01310-100
 * </pre>
 */
@Validated
@RestController
@RequestMapping("/api/v1")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    /**
     * GET /api/v1/cep/{cep}
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<CepResponse> consultarCep(
            @PathVariable
            @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos no formato 00000-000 ou 00000000")
            String cep) {

        CepData data = cepService.consultar(cep);
        return ResponseEntity.ok(CepResponse.from(data));
    }

    /**
     * GET /api/v1/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
