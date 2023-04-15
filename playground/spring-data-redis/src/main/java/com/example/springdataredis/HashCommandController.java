package com.example.springdataredis;

import com.example.springdataredis.HashEntity.EnumType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class HashCommandController {

  private final HashEntityRepository hashEntityRepository;
  private final NestedHashEntityRepository nestedHashEntityRepository;
  private final NestedNestedHashEntityRepository nestedNestedHashEntityRepository;

  @GetMapping("/test")
  void test() {
    // nested field를 가진 객체 조회 시 쿼리가 너무 많이 발생한다. 원래 hash 타입 목적에 맞게 one level depth 에서만 사용하자.
    NestedNestedHashEntity nestedNestedHashEntity1 = NestedNestedHashEntity.builder().id(1L).str("sample_string").build();
    NestedHashEntity nestedHashEntity1 = NestedHashEntity.builder().id(1L).str("sample_string").nestedNestedHashEntities(List.of(
        nestedNestedHashEntity1)).build();
    NestedHashEntity nestedHashEntity2 = NestedHashEntity.builder().id(2L).str("sample_string2").nestedNestedHashEntities(List.of(
        nestedNestedHashEntity1)).build();
    HashEntity hashEntity1 = HashEntity.builder()
        .id(1L)
        .str("sample_string")
        .bigDecimal(new BigDecimal("123.45"))
        .bool(true)
        .localDate(LocalDate.now())
        .enumType(EnumType.TEST)
        .list(List.of("list_element_1", "list_element_2"))
        .set(Set.of("set_element_1", "set_element_2"))
        .map(Map.of("map_key_1", "map_value_1", "map_key_2", "map_value_2"))
        .nestedHashEntities(List.of(nestedHashEntity1))
        .nestedHashEntities2(Set.of(nestedHashEntity2))
        .build();

    // "DEL" "com.example.springdataredis.HashEntity:1"
    // "HMSET" "com.example.springdataredis.HashEntity:1" "_class" "com.example.springdataredis.HashEntity" "bigDecimal" "123.45" "bool" "1" "enumType" "TEST" "id" "1" "list.[0]" "list_element_1" "list.[1]" "list_element_2" "localDate" "2023-04-15" "map.[map_key_1]" "map_value_1" "map.[map_key_2]" "map_value_2" "nestedHashEntities.[0]" "com.example.springdataredis.NestedHashEntity:1" "nestedHashEntities2.[0]" "com.example.springdataredis.NestedHashEntity:2" "set.[0]" "set_element_2" "set.[1]" "set_element_1" "str" "sample_string"
    // "SADD" "com.example.springdataredis.HashEntity" "1"
    // "SADD" "com.example.springdataredis.HashEntity:str:sample_string" "1"
    // "SADD" "com.example.springdataredis.HashEntity:1:idx" "com.example.springdataredis.HashEntity:str:sample_string"
    hashEntityRepository.save(hashEntity1);
    // "DEL" "com.example.springdataredis.NestedHashEntity:1"
    // "HMSET" "com.example.springdataredis.NestedHashEntity:1" "_class" "com.example.springdataredis.NestedHashEntity" "id" "1" "nestedNestedHashEntities.[0]" "com.example.springdataredis.NestedNestedHashEntity:1" "str" "sample_string"
    // "SADD" "com.example.springdataredis.NestedHashEntity" "1"
    // "DEL" "com.example.springdataredis.NestedHashEntity:2"
    // "HMSET" "com.example.springdataredis.NestedHashEntity:2" "_class" "com.example.springdataredis.NestedHashEntity" "id" "2" "nestedNestedHashEntities.[0]" "com.example.springdataredis.NestedNestedHashEntity:1" "str" "sample_string2"
    // "SADD" "com.example.springdataredis.NestedHashEntity" "2"
    nestedHashEntityRepository.saveAll(List.of(nestedHashEntity1, nestedHashEntity2));
    // "DEL" "com.example.springdataredis.NestedNestedHashEntity:1"
    // "HMSET" "com.example.springdataredis.NestedNestedHashEntity:1" "_class" "com.example.springdataredis.NestedNestedHashEntity" "id" "1" "str" "sample_string"
    // "SADD" "com.example.springdataredis.NestedNestedHashEntity" "1"
    nestedNestedHashEntityRepository.saveAll(List.of(nestedNestedHashEntity1));

    // "HGETALL" "com.example.springdataredis.HashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedHashEntity:2"
    // "HGETALL" "com.example.springdataredis.NestedNestedHashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedHashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedNestedHashEntity:1"
    HashEntity findHashEntity1 = hashEntityRepository.findById(1L).orElseThrow(RuntimeException::new);

    System.out.println(findHashEntity1);

    // "SINTER" "com.example.springdataredis.HashEntity:str:sample_string"
    // "HGETALL" "com.example.springdataredis.HashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedHashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedNestedHashEntity:1"
    // "HGETALL" "com.example.springdataredis.NestedHashEntity:2"
    // "HGETALL" "com.example.springdataredis.NestedNestedHashEntity:1"
    hashEntityRepository.findByStr("sample_string");
  }
}
