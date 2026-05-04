package br.com.joabe.cepapi.infra;

import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.infra.http.WireMockCepGateway;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração do adapter HTTP.
 * Sobe WireMock em memória para simular a API de CEP.
 */
@DisplayName("WireMockCepGateway — testes de integração")
class WireMockCepGatewayTest {

    private static WireMockServer wireMockServer;
    private WireMockCepGateway gateway;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        gateway = new WireMockCepGateway(
                RestClient.builder(),
                "http://localhost:" + wireMockServer.port()
        );
    }

    @Test
    @DisplayName("Deve retornar CepData quando CEP encontrado")
    void deveRetornarCepDataQuandoEncontrado() {
        wireMockServer.stubFor(get(urlEqualTo("/ws/01310100/json"))
                .willReturn(okJson("""
                    {
                      "cep": "01310-100",
                      "logradouro": "Avenida Paulista",
                      "complemento": "",
                      "bairro": "Bela Vista",
                      "localidade": "São Paulo",
                      "uf": "SP",
                      "ibge": "3550308"
                    }
                """)));

        Optional<CepData> resultado = gateway.buscar("01310100");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().cep()).isEqualTo("01310-100");
        assertThat(resultado.get().localidade()).isEqualTo("São Paulo");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando API retorna erro true")
    void deveRetornarVazioQuandoErroTrue() {
        wireMockServer.stubFor(get(urlEqualTo("/ws/99999999/json"))
                .willReturn(okJson("""
                    { "erro": "true" }
                """)));

        Optional<CepData> resultado = gateway.buscar("99999999");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando API retorna 404")
    void deveRetornarVazioQuando404() {
        wireMockServer.stubFor(get(urlEqualTo("/ws/00000000/json"))
                .willReturn(notFound()));

        Optional<CepData> resultado = gateway.buscar("00000000");

        assertThat(resultado).isEmpty();
    }
}
