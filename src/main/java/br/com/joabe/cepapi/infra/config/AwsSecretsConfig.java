package br.com.joabe.cepapi.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

/**
 * Carrega credenciais do banco via AWS Secrets Manager em produção.
 * Ativado somente quando aws.secrets.enabled=true.
 */
@Configuration
@ConditionalOnProperty(name = "aws.secrets.enabled", havingValue = "true")
public class AwsSecretsConfig {

    private static final Logger log = LoggerFactory.getLogger(AwsSecretsConfig.class);

    @Value("${aws.secrets.secret-name}")
    private String secretName;

    @Value("${aws.secrets.region}")
    private String region;

    @PostConstruct
    public void loadSecrets() {
        log.info("Carregando credenciais do AWS Secrets Manager: {}", secretName);
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region)).build()) {

            GetSecretValueResponse response = client.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretName).build());

            Map<?, ?> secrets = new ObjectMapper().readValue(response.secretString(), Map.class);
            System.setProperty("spring.datasource.url",      (String) secrets.get("url"));
            System.setProperty("spring.datasource.username", (String) secrets.get("username"));
            System.setProperty("spring.datasource.password", (String) secrets.get("password"));

            log.info("Credenciais carregadas com sucesso.");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar secrets da AWS", e);
        }
    }
}
