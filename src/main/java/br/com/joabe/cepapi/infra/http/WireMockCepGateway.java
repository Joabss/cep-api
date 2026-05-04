package br.com.joabe.cepapi.infra.http;

import br.com.joabe.cepapi.domain.exception.CepGatewayException;
import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.port.CepGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class WireMockCepGateway implements CepGateway {

    private static final Logger log = LoggerFactory.getLogger(WireMockCepGateway.class);

    private final RestClient restClient;

    public WireMockCepGateway(
            RestClient.Builder builder,
            @Value("${cep.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<CepData> buscar(String cep) {
        log.debug("Chamando API mock para CEP={}", cep);

        try {
            ResponseEntity<CepApiResponse> response = restClient.get()
                    .uri("/ws/{cep}/json", cep)
                    .retrieve()
                    .toEntity(CepApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                CepApiResponse body = response.getBody();

                // WireMock/ViaCEP retorna { "erro": "true" } para CEP não encontrado
                if ("true".equals(body.erro()) || body.cep() == null || body.cep().isBlank()) {
                    log.warn("CEP={} não encontrado na API", cep);
                    return Optional.empty();
                }

                return Optional.of(toModel(body));
            }

            return Optional.empty();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("CEP={} retornou 404", cep);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Erro ao consultar CEP={}: {}", cep, e.getMessage());
            throw new CepGatewayException("Falha ao consultar API de CEP: " + e.getMessage(), e);
        }
    }

    /** Converte DTO interno para objeto de domínio. */
    private CepData toModel(CepApiResponse r) {
        return new CepData(
                r.cep(), r.logradouro(), r.complemento(),
                r.bairro(), r.localidade(), r.uf(),
                r.ibge()
        );
    }
}
