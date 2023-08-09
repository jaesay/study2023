package com.example.springdatareactiveredis;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRedisRepository extends CustomReactiveRedisRepository<Employee, String> {

  public EmployeeRedisRepository(ReactiveRedisConnectionFactory factory) {
    super(factory, Employee.class, "employee");
  }
}
