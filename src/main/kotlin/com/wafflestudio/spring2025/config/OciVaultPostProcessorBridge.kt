package com.wafflestudio.spring2025.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.oracle.bmc.Region
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider
import com.oracle.bmc.secrets.SecretsClient
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import java.util.Base64

/**
 * OCI Vault EnvironmentPostProcessor that replaces the broken waffle-oci-vault library.
 * The library (2.1.0) implements org.springframework.boot.EnvironmentPostProcessor (wrong package)
 * instead of org.springframework.boot.env.EnvironmentPostProcessor, making it incompatible
 * with Spring Boot 3.x.
 */
class OciVaultPostProcessorBridge : EnvironmentPostProcessor {
    private val objectMapper = jacksonObjectMapper()

    override fun postProcessEnvironment(
        environment: ConfigurableEnvironment,
        application: SpringApplication,
    ) {
        val secretIds = environment.getProperty("oci.vault.secret-ids") ?: return
        val regionId = environment.getProperty("oci.vault.region", "ap-chuncheon-1")
        val region = Region.fromRegionId(regionId)

        val authProvider =
            try {
                InstancePrincipalsAuthenticationDetailsProvider.builder().build()
            } catch (e: Exception) {
                System.err.println("[OciVault] Instance Principal auth failed: ${e.message}")
                return
            }

        val secrets = mutableMapOf<String, Any>()

        SecretsClient.builder().region(region).build(authProvider).use { client ->
            for (secretId in secretIds.split(",").map { it.trim() }) {
                try {
                    val request =
                        GetSecretBundleRequest
                            .builder()
                            .secretId(secretId)
                            .build()
                    val response = client.getSecretBundle(request)
                    val content =
                        response.secretBundle.secretBundleContent
                            as? Base64SecretBundleContentDetails ?: continue
                    val decoded = String(Base64.getDecoder().decode(content.content))
                    val parsed: Map<String, String> = objectMapper.readValue(decoded)

                    for ((key, value) in parsed) {
                        val existing = environment.getProperty(key)
                        if (existing.isNullOrBlank()) {
                            secrets[key] = value
                        }
                    }
                } catch (e: Exception) {
                    System.err.println("[OciVault] Failed to fetch secret $secretId: ${e.message}")
                }
            }
        }

        if (secrets.isNotEmpty()) {
            environment.propertySources.addFirst(MapPropertySource("oci-vault-secrets", secrets))
        }
    }
}
