package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.models.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe,
                             RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("In the service");

        return recipeRepository
                .findAll()
                .switchIfEmpty(Flux.error(new NotFoundException("No Recipe Found!")));
    }

    @Override
    public Mono<Recipe> findById(String id) {
        return recipeRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Recipe Not Found. For ID: " + id)));
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);
        return recipeRepository.save(detachedRecipe)
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> findCommandById(String id) {

        return recipeRepository
                .findById(id)
                .map(recipe -> {
                    RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
                    recipeCommand.getIngredients().forEach(ingredientCommand ->
                            ingredientCommand.setRecipeId(recipeCommand.getId()));
                    return recipeCommand;
                })
                .switchIfEmpty(Mono.error(new NotFoundException("RecipeCommand Not Found!")));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        recipeRepository.deleteById(id);
        return Mono.empty();
    }
}
