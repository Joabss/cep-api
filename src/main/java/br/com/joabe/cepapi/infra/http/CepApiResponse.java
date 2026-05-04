package br.com.joabe.cepapi.infra.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO interno usado apenas para desserializar a resposta JSON da API mock.
 * Nunca sai deste pacote — o adapter converte para CepData (domínio).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record CepApiResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String erro
) {}
