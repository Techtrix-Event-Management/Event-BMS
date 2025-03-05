package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.QRData;
import com.example.demo.Model.SponsorImage;
import com.example.demo.Model.Sponsors;
import com.example.demo.repo.QRDataRepo;
import com.example.demo.repo.SponsorImageRepository;
import com.example.demo.repo.SponsorRepository;

import io.jsonwebtoken.io.IOException;

@Service
public class SponsorService {

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private QRDataRepo qrDataRepo;

        @Autowired
        private SponsorImageRepository sponsorImageRepository;

        public Sponsors addSponsor(String name, String websiteUrl, MultipartFile logo, List<MultipartFile> images) throws IOException, java.io.IOException {
            Sponsors sponsor = new Sponsors();
            sponsor.setName(name);
            sponsor.setWebsiteUrl(websiteUrl);

            if (logo != null && !logo.isEmpty() && isValidImage(logo)) {
                sponsor.setLogo(logo.getBytes()); // Store logo as byte[]
            }

            Sponsors savedSponsor = sponsorRepository.save(sponsor);

            List<SponsorImage> sponsorImages = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty() && isValidImage(image)) {
                    SponsorImage sponsorImage = new SponsorImage();
                    sponsorImage.setImageData(image.getBytes()); // Store image as byte[]
                    sponsorImage.setSponsor(savedSponsor);
                    sponsorImages.add(sponsorImage);
                }
            }

            sponsorImageRepository.saveAll(sponsorImages);
            return savedSponsor;
        }

        private boolean isValidImage(MultipartFile file) {
            String contentType = file.getContentType();
            return contentType.equals("image/png") || contentType.equals("image/jpeg");
        }

        public List<Sponsors> getAllSponsors() {
            return sponsorRepository.findAll();
        }
        
        
        public void deleteSponsor(Long id) {
            if (sponsorRepository.existsById(id)) {
                sponsorRepository.deleteById(id);
            } else {
                throw new RuntimeException("Sponsor not found with ID: " + id);
            }
        }
        
       

}