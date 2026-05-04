package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.service.CepService;
import br.com.joabe.cepapi.dto.CepResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            String cep,
            HttpServletRequest request) {

        CepData data = cepService.consultar(cep);
        return ResponseEntity.ok(CepResponse.from(data));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
