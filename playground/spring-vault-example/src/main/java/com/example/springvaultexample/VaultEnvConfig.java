package com.example.springvaultexample;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.vault.config.EnvironmentVaultConfiguration;

@Configuration
@Import(value = EnvironmentVaultConfiguration.class)
public class VaultEnvConfig {

}
