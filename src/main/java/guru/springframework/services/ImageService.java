package guru.springframework.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    void saveImage(String recipeId, MultipartFile file);
}
