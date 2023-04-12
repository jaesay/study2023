package com.example.springlettucemod;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisJSONCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandExampleController {

  private final RedisJSONCommands<String, String> commands;

  public CommandExampleController(StatefulRedisModulesConnection<String, String> connection) {
    this.commands = connection.sync();
  }

  @GetMapping("/animal")
  public String animal() {
    commands.jsonSet("animal", "$", "\"dog\"");
    String animal = commands.jsonGet("animal");
    String type = commands.jsonType("animal");
    return String.format("value: %s, type: %s", animal, type);
  }
}
