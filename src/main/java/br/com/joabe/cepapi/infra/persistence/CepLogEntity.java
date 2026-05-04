package br.com.joabe.cepapi.infra.persistence;

import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cep_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CepLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(length = 255)
    private String logradouro;

    @Column(length = 255)
    private String complemento;

    @Column(length = 255)
    private String bairro;

    @Column(length = 255)
    private String localidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 20)
    private String ibge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConsultaStatus status;

    @Column(name = "error_msg", length = 500)
    private String errorMsg;

    @Column(name = "consulted_at", nullable = false)
    private LocalDateTime consultedAt;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;
}