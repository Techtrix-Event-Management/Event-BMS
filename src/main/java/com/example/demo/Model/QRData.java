package com.example.demo.Model;




import jakarta.persistence.*;


@Entity

@Table(name = "qr_data")
public class QRData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String upiId;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrImage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUpiId() {
		return upiId;
	}

	public void setUpiId(String upiId) {
		this.upiId = upiId;
	}

	public byte[] getQrImage() {
		return qrImage;
	}

	public void setQrImage(byte[] qrImage) {
		this.qrImage = qrImage;
	}
    
    
}