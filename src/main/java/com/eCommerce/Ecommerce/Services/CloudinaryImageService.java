package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import java.util.*;

@Service
public class CloudinaryImageService {

    @Autowired
    private Cloudinary cloudinary;

    public Map upload(MultipartFile file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("Image file must not be null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }
        return cloudinary.uploader().upload(file.getBytes(), Map.of());
    }
}
