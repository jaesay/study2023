package com.optimagrowth.license.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

  private final LicenseService licenseService;

  @GetMapping(value = "/{licenseId}")
  public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {

    License license = licenseService.getLicense(licenseId, organizationId);
    license.add(
        linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId())).withSelfRel(),
        linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null)).withRel("createLicense"),
        linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license)).withRel("updateLicense"),
        linkTo(methodOn(LicenseController.class).deleteLicense(organizationId, license.getLicenseId())).withRel("deleteLicense")
    );

    return ResponseEntity.ok(license);
  }

  @PostMapping
  public ResponseEntity<String> createLicense(@PathVariable("organizationId") String organizationId, @RequestBody License request,
      @RequestHeader(value = "Accept-Language",required = false) Locale locale) {
    return ResponseEntity.ok(licenseService.createLicense(request, organizationId, locale));
  }

  @PutMapping
  public ResponseEntity<String> updateLicense(@PathVariable("organizationId") String organizationId, @RequestBody License request) {
    return ResponseEntity.ok(licenseService.updateLicense(request, organizationId));
  }

  @DeleteMapping(value="/{licenseId}")
  public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") String organizationId, @PathVariable("licenseId") String licenseId) {
    return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
  }
}
