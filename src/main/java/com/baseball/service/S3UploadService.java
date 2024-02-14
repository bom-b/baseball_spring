package com.baseball.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveWithImageInputStream(String path, String fileName, InputStream imageInputStream) {
        try {

            // Thumbnailator를 사용하여 이미지 크기를 조정합니다.
            Builder<? extends InputStream> thumbnail = Thumbnails.of(imageInputStream).height(500);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            thumbnail.outputFormat("jpg").toOutputStream(os);
            byte[] resizedImageBytes = os.toByteArray();

            // 조정된 이미지를 ByteArrayInputStream으로 변환합니다.
            ByteArrayInputStream is = new ByteArrayInputStream(resizedImageBytes);

            String fullPath = path + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.setContentLength(resizedImageBytes.length);

            // ByteArrayInputStream을 S3에 업로드합니다.
            amazonS3.putObject(new PutObjectRequest(bucket, fullPath, is, metadata));

            return amazonS3.getUrl(bucket, fullPath).toString();

        } catch (Exception e) {
            return null;
        }
    }

    public String saveWithMultipartFile(String path, String fileName, MultipartFile file) {
        try {
            InputStream imageInputStream = file.getInputStream();
            return saveWithImageInputStream(path, fileName, imageInputStream);
        } catch (IOException e) {
            return null;
        }
    }

    public void deleteFileFromS3(String filePath) {
        String fullFilePath = "s3://" + bucket + "/" + filePath;
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }
}
