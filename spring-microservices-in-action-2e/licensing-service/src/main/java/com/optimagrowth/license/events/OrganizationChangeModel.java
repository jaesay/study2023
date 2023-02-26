package com.optimagrowth.license.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class OrganizationChangeModel {
  private String type;
  private String action;
  private String organizationId;
  private String correlationId;
}
