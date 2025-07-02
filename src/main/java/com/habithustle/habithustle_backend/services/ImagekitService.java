package com.habithustle.habithustle_backend.services;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagekitService {

    @Autowired
    private ImageKit imagekit;

    public String uploadProfile(MultipartFile file) throws Exception{
        FileCreateRequest request=new FileCreateRequest(file.getBytes(), file.getOriginalFilename());
        request.setFolder("/user-profiles");
        request.setUseUniqueFileName(true);

        Result result=imagekit.upload(request);

        if (result.getUrl() == null || result.getFileId() == null) {
            throw new RuntimeException("Image upload failed: no URL or File ID returned.");
        }

        return result.getUrl();
    }

    public String uploadProof(MultipartFile file) {
        try {
            FileCreateRequest request = new FileCreateRequest(file.getBytes(), file.getOriginalFilename());
            request.setFolder("/user-proof");
            request.setUseUniqueFileName(true);

            Result result = imagekit.upload(request);

            if (result.getUrl() == null || result.getFileId() == null) {
                throw new RuntimeException("Image upload failed: no URL or File ID returned.");
            }

            return result.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Image upload error: " + e.getMessage(), e);
        }
    }



}
