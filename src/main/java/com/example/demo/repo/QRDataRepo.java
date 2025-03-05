package com.example.demo.repo;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.QRData;

public interface QRDataRepo extends JpaRepository<QRData, Long> {
}