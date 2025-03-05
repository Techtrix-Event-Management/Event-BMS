package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.SponsorImage;
import com.example.demo.Model.Sponsors;
import com.example.demo.service.SponsorService;

import io.jsonwebtoken.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsors")
public class SponsorController {

    @Autowired
    private SponsorService sponsorService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sponsors> addSponsor(
        @RequestParam("name") String name,
        @RequestParam("websiteUrl") String websiteUrl,
        @RequestParam(value = "logo", required = false) MultipartFile logo,
        @RequestParam("images") List<MultipartFile> images) throws IOException, java.io.IOException {

        try {
            Sponsors sponsor = sponsorService.addSponsor(name, websiteUrl, logo, images);
            return ResponseEntity.ok(sponsor);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllSponsors() {
        List<Sponsors> sponsors = sponsorService.getAllSponsors();
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Sponsors sponsor : sponsors) {
            Map<String, Object> sponsorData = new HashMap<>();
            sponsorData.put("id", sponsor.getId());
            sponsorData.put("name", sponsor.getName());
            sponsorData.put("websiteUrl", sponsor.getWebsiteUrl());
            sponsorData.put("logo", sponsor.getLogoBase64());

            List<String> imagesBase64 = new ArrayList<>();
            for (SponsorImage image : sponsor.getImages()) {
                imagesBase64.add(image.getImageBase64());
            }
            sponsorData.put("images", imagesBase64);

            responseList.add(sponsorData);
        }

        return ResponseEntity.ok(responseList);
    }
    
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSponsor(@PathVariable Long id) {
        try {
            sponsorService.deleteSponsor(id);
            return ResponseEntity.ok().body("Sponsor deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}
