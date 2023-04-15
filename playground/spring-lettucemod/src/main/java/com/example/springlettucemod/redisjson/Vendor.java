package com.example.springlettucemod.redisjson;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Vendor {

  private Long id;
  private String name;
  private String phone;
  private boolean currentlyOpen;
  private List<Menu> menus;
  private List<Location> locations;
  private int waitTime;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Menu {
    private String name;
    private BigDecimal price;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Location {
    private String address;
    private List<BigDecimal> coordinates;
  }

  public String key() {
    return "vendor:" + this.id;
  }
}
