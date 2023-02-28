package com.optimagrowth.license.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.utils.UserContextHolder;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

  private final LicenseService licenseService;

  @GetMapping
  public List<License> getLicenses(@PathVariable("organizationId") String organizationId) throws TimeoutException {
    log.debug("LicenseController#getLicenses > Thread Name: {}, Correlation Id: {}", Thread.currentThread().getName(), UserContextHolder.getContext().getCorrelationId());
    return licenseService.getLicensesByOrganization(organizationId);
  }

  @GetMapping(value = "/{licenseId}/{clientType}")
  public License getLicensesWithClient(@PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId,
      @PathVariable("clientType") String clientType) {

    return licenseService.getLicense(licenseId, organizationId, clientType);
  }

  @GetMapping(value = "/{licenseId}")
  public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {

    License license = licenseService.getLicense(licenseId, organizationId);
    license.add(
        linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId())).withSelfRel(),
        linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
        linkTo(methodOn(LicenseController.class).updateLicense(license)).withRel("updateLicense"),
        linkTo(methodOn(LicenseController.class).deleteLicense(license.getLicenseId())).withRel("deleteLicense")
    );

    return ResponseEntity.ok(license);
  }

  @PostMapping
  public ResponseEntity<License> createLicense(@RequestBody License request) {
    return ResponseEntity.ok(licenseService.createLicense(request));
  }

  @PutMapping
  public ResponseEntity<License> updateLicense(@RequestBody License request) {
    return ResponseEntity.ok(licenseService.updateLicense(request));
  }

  @DeleteMapping(value = "/{licenseId}")
  public ResponseEntity<String> deleteLicense(@PathVariable("licenseId") String licenseId) {
    return ResponseEntity.ok(licenseService.deleteLicense(licenseId));
  }
}
