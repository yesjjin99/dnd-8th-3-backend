package d83t.bpmbackend.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3UploaderService {

    String putS3(MultipartFile uploadFile, String path, String fileName);
    void deleteS3Image(String fullPath);
}
