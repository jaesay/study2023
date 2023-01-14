package com.example.springvaultexample;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.vault.VaultContainer;

@SpringBootTest
class CredentialsServiceTest {

  private static final String VAULT_TOKEN = "my-root-token";

  @Autowired
  CredentialsService credentialsService;

  static VaultContainer<?> vaultContainer = new VaultContainer<>("vault:1.6.1")
      .withVaultToken(VAULT_TOKEN)
      .withSecretInVault("secret/myapp", "username=jaesay", "password=1234")
      .withInitCommand("secrets enable transit", "write -f transit/keys/my-key");

  @DynamicPropertySource
  static void setUpRedis(DynamicPropertyRegistry registry) {
    Startables.deepStart(vaultContainer).join();

    registry.add("vault.uri", vaultContainer::getHttpHostAddress);
    registry.add("vault.token", () -> VAULT_TOKEN);
  }

  @Test
  void test() throws IOException, InterruptedException {
    GenericContainer.ExecResult result = vaultContainer.execInContainer(
        "vault",
        "kv",
        "get",
        "-format=json",
        "secret/myapp"
    );
    assertThat(result.getStdout()).contains("jaesay");
  }

  @Test
  void test2() throws URISyntaxException {
    Credentials credentials = credentialsService.accessCredentials();
    assertThat(credentials.getUsername()).isEqualTo("jaesay");
    assertThat(credentials.getPassword()).isEqualTo("1234");
  }

}