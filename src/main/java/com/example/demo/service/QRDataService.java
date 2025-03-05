package com.example.demo.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.QRData;
import com.example.demo.repo.QRDataRepo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class QRDataService {
    @Autowired
    private QRDataRepo qrDataRepository;

    public QRData saveQRData(String upiId, MultipartFile qrImage) throws IOException {
        QRData qrData = new QRData();
        qrData.setUpiId(upiId);
        qrData.setQrImage(qrImage.getBytes());
        return qrDataRepository.save(qrData);
    }

    
    public List<QRData> getAllQRData() {
        return qrDataRepository.findAll();
    }
}