package com.example.springbootac;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootAcApplication {

  @Bean
  ApplicationRunner run(ConditionEvaluationReport report) {
    /* 자동구성 클래스 Condition 결과 확인  */
    return args -> {
      System.out.println(report.getConditionAndOutcomesBySource().entrySet().stream()
          .filter(co -> co.getValue().isFullMatch())
          .filter(co -> !co.getKey().contains("Jmx"))
          .map(co -> {
            System.out.println(co.getKey());
            co.getValue().forEach(v -> {
              System.out.println("\t" + v.getOutcome());
            });
            System.out.println();
            return co;
          }).count());
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringbootAcApplication.class, args);
  }

}
