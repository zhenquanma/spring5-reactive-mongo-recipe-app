package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.models.Ingredient;
import guru.springframework.models.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final RecipeReactiveRepository recipeReactiveRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository,
                                 RecipeReactiveRepository recipeReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String id) {
        return recipeReactiveRepository.findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equals(id))
                .single()
                .map(ingredient -> {
                    IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
                    ingredientCommand.setRecipeId(recipeId);
                    return ingredientCommand;
                });
    }

    @Override
    @Transactional
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand) {
        Recipe recipe = recipeReactiveRepository.findById(ingredientCommand.getRecipeId()).block();

        if(recipe == null) {
            log.error("Recipe not found for id: " + ingredientCommand.getRecipeId());
            return Mono.just(new IngredientCommand());
        }

        Optional<Ingredient> ingredientOptional = recipe.getIngredients()
                .stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                .findFirst();

        if(!ingredientOptional.isPresent()) {
            //Add new
            Ingredient ingredient = ingredientCommandToIngredient.convert(ingredientCommand);
//            ingredient.setUom(unitOfMeasureRepository.findById(ingredientCommand.getUom().getId())
//                    .orElseThrow(() -> new RuntimeException("UOM NOT FOUND")));
            recipe.addIngredient(ingredient);
        }
        else{
            //Update it
            Ingredient ingredient = ingredientOptional.get();
            ingredient.setDescription(ingredientCommand.getDescription());
            ingredient.setUom(unitOfMeasureRepository.findById(ingredientCommand.getUom().getId()).block());
            ingredient.setAmount(ingredientCommand.getAmount());
        }

        Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                .findFirst();

        /* Check by description. IngredientCommand may not has a id yet */
        if(!savedIngredientOptional.isPresent()) {
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getDescription().equals(ingredientCommand.getDescription()))
                    .filter(ingredient -> ingredient.getAmount().equals(ingredientCommand.getAmount()))
                    .filter(ingredient -> ingredient.getUom().getId().equals(ingredientCommand.getUom().getId()))
                    .findFirst();
        }


        IngredientCommand savedCommand = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        savedCommand.setRecipeId(recipe.getId());

        return Mono.just(savedCommand);
    }

    @Override
    public Mono<Void> deleteByRecipeIdAndIngredientId(String recipeId, String id) {
        Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

         if(recipe == null) {
            throw new RuntimeException("RECIPE NOT FOUND!");
        }

        Optional<Ingredient> ingredientOptional = recipe.getIngredients()
                .stream()
                .filter(ingredient -> ingredient.getId().equals(id))
                .findFirst();

        if(!ingredientOptional.isPresent()) {
            throw new RuntimeException("INGREDIENT NOT FOUND");
        }

        recipe.getIngredients().remove(ingredientOptional.get());
        recipeReactiveRepository.save(recipe).block();

        return Mono.empty();
    }

}
