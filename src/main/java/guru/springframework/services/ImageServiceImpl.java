package guru.springframework.services;

import guru.springframework.models.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<Void> saveImage(String recipeId, MultipartFile file) {
        log.debug("Saving an image");

        Mono<Recipe> recipeMono = recipeRepository.findById(recipeId)
                .map(recipe -> {
                    Byte[] bytes;
                    try {
                        bytes = new Byte[file.getBytes().length];

                        int i = 0;

                        for (byte b : file.getBytes()) {
                            bytes[i++] = b;
                        }

                        recipe.setImage(bytes);

                        return recipe;

                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("RECIPE NOT FOUND")));

        recipeRepository.save(recipeMono.block()).block();

        return Mono.empty();
    }
}
