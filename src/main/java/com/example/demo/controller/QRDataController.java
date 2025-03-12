package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.QRData;
import com.example.demo.service.QRDataService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/qr")
public class QRDataController {
    
    @Autowired
    private QRDataService qrDataService;

    @PostMapping(value = "/postqr",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QRData> saveQRData(@RequestParam("upiId") String upiId,
                                             @RequestParam("qrImage") MultipartFile qrImage) throws IOException {
        QRData savedQR = qrDataService.saveQRData(upiId, qrImage);
        return ResponseEntity.ok(savedQR);
    }

    @GetMapping("/getall")
    public List<QRData> getAllQRData() {
        return qrDataService.getAllQRData();
    }
}
