package com.hanghae.hackathon.Ant.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class FileUtils {
    private final AmazonS3 amazonS3;

    public FileUtils(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public String upload(MultipartFile multipartFile) {
        String filename = makeFileName();
        String fileExtension = getFileExtension(multipartFile.getOriginalFilename());
        String fileKey = String.join("", filename, fileExtension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        try {
            amazonS3.putObject(
                    new PutObjectRequest(
                            bucketName,
                            fileKey,
                            multipartFile.getInputStream(),
                            metadata
                    ).withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileKey;
    }

    private String makeFileName() {

        return String.join(
                "-",
                LocalDate.now(ZoneId.of("Asia/Seoul")).toString(),
                UUID.randomUUID().toString());
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
