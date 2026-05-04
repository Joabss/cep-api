package br.com.joabe.cepapi.domain;

import br.com.joabe.cepapi.domain.exception.CepNotFoundException;
import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.port.CepGateway;
import br.com.joabe.cepapi.domain.port.CepLogPort;
import br.com.joabe.cepapi.domain.service.CepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do domínio.
 * Sem Spring, sem banco, sem HTTP — roda em milissegundos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CepService — testes unitários do domínio")
class CepServiceTest {

    @Mock
    private CepGateway cepGateway;

    @Mock
    private CepLogPort cepLogPort;

    @InjectMocks
    private CepService cepService;

    private CepData cepDataMock;

    @BeforeEach
    void setUp() {
        cepDataMock = new CepData(
                "01310-100", "Avenida Paulista", "de 1 a 610",
                "Bela Vista", "São Paulo", "SP", "3550308"
        );
    }

    @Test
    @DisplayName("Deve retornar CepData e salvar log de sucesso")
    void deveRetornarDadosESalvarLogSucesso() {
        when(cepGateway.buscar("01310100")).thenReturn(Optional.of(cepDataMock));

        CepData resultado = cepService.consultar("01310100");

        assertThat(resultado).isNotNull();
        assertThat(resultado.cep()).isEqualTo("01310-100");
        assertThat(resultado.uf()).isEqualTo("SP");

        verify(cepGateway).buscar("01310100");
        verify(cepLogPort).salvar(argThat(log ->
                log.getStatus().name().equals("SUCCESS") &&
                log.getCep().equals("01310100")
        ));
    }

    @Test
    @DisplayName("Deve lançar CepNotFoundException e salvar log NOT_FOUND")
    void deveLancarExcecaoQuandoCepNaoEncontrado() {
        when(cepGateway.buscar("99999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cepService.consultar("99999999"))
                .isInstanceOf(CepNotFoundException.class)
                .hasMessageContaining("99999999");

        verify(cepLogPort).salvar(argThat(log ->
                log.getStatus().name().equals("NOT_FOUND")
        ));
    }

    @Test
    @DisplayName("Deve normalizar CEP com máscara antes de consultar")
    void deveNormalizarCepComMascara() {
        when(cepGateway.buscar("01310100")).thenReturn(Optional.of(cepDataMock));

        cepService.consultar("01310-100");

        verify(cepGateway).buscar("01310100");
    }

    @Test
    @DisplayName("Deve salvar log de erro quando gateway lança exceção")
    void deveSalvarLogErroQuandoGatewayFalha() {
        when(cepGateway.buscar(any())).thenThrow(new RuntimeException("timeout"));

        assertThatThrownBy(() -> cepService.consultar("01310100"))
                .isInstanceOf(RuntimeException.class);

        verify(cepLogPort).salvar(argThat(log ->
                log.getStatus().name().equals("ERROR")
        ));
    }
}
