package guru.springframework.services;


import guru.springframework.commands.RecipeCommand;
import guru.springframework.models.Recipe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {

    Flux<Recipe> getRecipes();

    Mono<Recipe> findById(String id);

    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);

    Mono<RecipeCommand> findCommandById(String commandId);

    Mono<Void> deleteById(String id);
}
