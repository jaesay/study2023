package com.example.springlettucemod.redisjson;

import static com.example.springlettucemod.redisjson.Vendor.Location;
import static com.example.springlettucemod.redisjson.Vendor.Menu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisJSONCommands;
import com.redis.lettucemod.json.ArrpopOptions;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redisjson")
public class RedisJsonController {

  private final RedisJSONCommands<String, String> commands;
  private final ObjectMapper objectMapper;

  public RedisJsonController(StatefulRedisModulesConnection<String, String> connection,
      ObjectMapper objectMapper) {
    this.commands = connection.sync();
    this.objectMapper = objectMapper;
  }

  @GetMapping("/test")
  public void test() throws JsonProcessingException {
    Menu burrito = new Menu("burrito", new BigDecimal("11.5"));
    Menu taco = new Menu("taco", new BigDecimal("3.5"));
    Menu quesadilla = new Menu("quesadilla", new BigDecimal("6"));
    Menu torta = new Menu("Torta", new BigDecimal("4.5"));
    Location location = new Location("1434 1st Ave, Oakland, CA 94606", List.of(new BigDecimal("37.7989708"), new BigDecimal("-122.2565053")));

    Vendor vendor = Vendor.builder()
        .id(1L)
        .name("Tacos Mi Rarchos")
        .phone("5557891234")
        .currentlyOpen(true)
        .menus(List.of(burrito, taco, quesadilla))
        .build();

    // "JSON.SET" "vendor:1" "." "{\"id\":1,\"name\":\"Tacos Mi Rarchos\",\"phone\":\"5557891234\",\"currentlyOpen\":true,\"menus\":[{\"name\":\"burrito\",\"price\":11.5},{\"name\":\"taco\",\"price\":3.5},{\"name\":\"quesadilla\",\"price\":6}],\"locations\":null,\"waitTime\":0}"
    commands.jsonSet(vendor.key(), ".", objectMapper.writeValueAsString(vendor));
    // "JSON.SET" "vendor:1" ".location" "{\"address\":\"1434 1st Ave, Oakland, CA 94606\",\"coordinates\":[37.7989708,-122.2565053]}"
    commands.jsonSet(vendor.key(), ".location", objectMapper.writeValueAsString(location));
    // "JSON.SET" "vendor:1" ".location.address" "\"1452 1st Ave, Oakland, CA 94505\""
    commands.jsonSet(vendor.key(), ".location.address", "\"1452 1st Ave, Oakland, CA 94505\"");

    // "JSON.GET" "vendor:1"
    System.out.println(commands.jsonGet(vendor.key()));
    // "JSON.GET" "vendor:1" ".menus"
    System.out.println(commands.jsonGet(vendor.key(), ".menus"));
    // "JSON.ARRAPPEND" "vendor:1" ".menus" "{\"name\":\"Torta\",\"price\":4.5}"
    commands.jsonArrappend(vendor.key(), ".menus", objectMapper.writeValueAsString(torta));
    // "JSON.ARRPOP" "vendor:1" ".menus" "0"
    commands.jsonArrpop(vendor.key(), ArrpopOptions.path(".menus").index(0));
    // "JSON.GET" "vendor:1" ".menus"
    System.out.println(commands.jsonGet(vendor.key(), ".menus"));

    // "JSON.NUMINCRBY" "vendor:1" ".waitTime" "10.0"
    commands.jsonNumincrby(vendor.key(), ".waitTime", 10);
    // "JSON.GET" "vendor:1" ".waitTime"
    System.out.println(commands.jsonGet(vendor.key(), ".waitTime"));
  }
}
