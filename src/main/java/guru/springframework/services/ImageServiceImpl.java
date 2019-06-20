package guru.springframework.services;

import guru.springframework.models.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void saveImage(String recipeId, MultipartFile file) {
        log.debug("Saving an image");

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if(!recipeOptional.isPresent()) {
            throw new RuntimeException("RECIPE NOT FOUND");
        }

        Recipe recipe = recipeOptional.get();

        try{
            Byte[] bytes = new Byte[file.getBytes().length];

            int i = 0;

            for(byte b : file.getBytes()){
                bytes[i] = b;
                i++;
            }

            recipe.setImage(bytes);

            recipeRepository.save(recipe);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
