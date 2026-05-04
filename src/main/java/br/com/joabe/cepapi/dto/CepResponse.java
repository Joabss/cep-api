package br.com.joabe.cepapi.dto;

import br.com.joabe.cepapi.domain.model.CepData;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO de resposta para os dados do CEP.
 * Mapeia os campos retornados pela API ViaCEP.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge
) {

    public static CepResponse from(CepData data) {
        return new CepResponse(
                data.cep(),
                data.logradouro(),
                data.complemento(),
                data.bairro(),
                data.localidade(),
                data.uf(),
                data.ibge()
        );
    }
}
