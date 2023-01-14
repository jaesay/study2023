package com.example.springvaultexample;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

//@Configuration
public class VaultConfig extends AbstractVaultConfiguration {
  @Value("${vault.props.schema}")
  private String schema;

  @Value("${vault.props.host}")
  private String host;

  @Value("${vault.props.port}")
  private String port;

  @Value("${vault.props.token}")
  private String token;

  @Override
  public VaultEndpoint vaultEndpoint() {
    return VaultEndpoint.from(URI.create(String.format("%s://%s:%s", schema, host, port)));
//    return VaultEndpoint.create("127.0.0.1", 8200);
  }

  @Override
  public ClientAuthentication clientAuthentication() {
    return new TokenAuthentication(token);
  }
}
