package com.example.springvaultexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

@Service
public class CredentialsService {
  private final VaultTemplate vaultTemplate;
  private final ObjectMapper objectMapper;

  public CredentialsService(VaultTemplate vaultTemplate, ObjectMapper objectMapper) {
    this.vaultTemplate = vaultTemplate;
    this.objectMapper = objectMapper;
  }

//  public void secureCredentials(Credentials credentials) throws URISyntaxException {
//    vaultTemplate.write("credentials/data/myapp2", credentials);
//  }

  public Credentials accessCredentials() throws URISyntaxException {
    VaultResponse response = vaultTemplate.read("secret/data/myapp?version=1");
//    VaultResponse response = vaultTemplate.read("credentials/data/myapp?version=1");
    return objectMapper.convertValue(response.getData().get("data"), Credentials.class);
  }

}
