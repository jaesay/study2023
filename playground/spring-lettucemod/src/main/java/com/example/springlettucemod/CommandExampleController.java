package com.example.springlettucemod;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisJSONCommands;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CommandExampleController {

  private final RedisJSONCommands<String, String> commands;

  public CommandExampleController(StatefulRedisModulesConnection<String, String> connection) {
    this.commands = connection.sync();
  }

  @GetMapping("/string")
  public void animal() {
    String key = "animal";
    String jsonSet = commands.jsonSet(key, "$", "\"dog\"");
    log.info("jsonSet: " + jsonSet);
    String jsonGet = commands.jsonGet(key);
    log.info("jsonGet: " + jsonGet);
    String jsonType = commands.jsonType(key);
    log.info("jsonType: " + jsonType);
    Long jsonStrlen = commands.jsonStrlen(key, "$");
    log.info("jsonStrlen: " + jsonStrlen);
    Long jsonStrappend = commands.jsonStrappend(key, "\" (Canis familiaris)\"");
    log.info("jsonStrappend: " + jsonStrappend);
    String jsonGet2 = commands.jsonGet(key);
    log.info("jsonGet2: " + jsonGet2);
    String jsonGet3 = commands.jsonGet(key, "$");
    log.info("jsonGet3: " + jsonGet3);
    // JSON.DEBUG MEMORY animal => 30
  }

  @GetMapping("/obj")
  public void obj() {
    String jsonSet = commands.jsonSet("obj", "$",
        "{\"name\":\"Leonard Cohen\",\"lastSeen\":1478476800,\"loggedOut\": true}");
    log.info("jsonSet: " + jsonSet);
    Long jsonObjlen = commands.jsonObjlen("obj", "$");
    log.info("jsonObjlen: " + jsonObjlen);
    List<String> jsonObjkeys = commands.jsonObjkeys("obj", "$");
    log.info("jsonObjkeys: " + jsonObjkeys);
  }

}
