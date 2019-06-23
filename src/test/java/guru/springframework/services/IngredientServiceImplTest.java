package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.models.Ingredient;
import guru.springframework.models.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;

    IngredientService ingredientService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient,
                unitOfMeasureRepository, recipeReactiveRepository);
    }

    @Test
    public void findByRecipeIdAndIngredientId() {

        Recipe recipe = new Recipe();
        recipe.setId("1");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("2");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "1").block();

        assertNotNull(ingredientCommand);
        assertEquals("1", ingredientCommand.getId());
        verify(recipeReactiveRepository).findById(anyString());
    }

    @Test
    public void saveIngredientCommand() {

        IngredientCommand commandToBeSaved = new IngredientCommand();
        commandToBeSaved.setId("1");
        commandToBeSaved.setRecipeId("2");

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId("1");

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(savedIngredient);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(new Recipe()));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(savedRecipe));

        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(commandToBeSaved).block();

        assertEquals("1", savedCommand.getId());
        verify(recipeReactiveRepository).findById(anyString());
        verify(recipeReactiveRepository).save(any(Recipe.class));

    }

    @Test
    public void testDeleteByRecipeIdAndIngredientId() {

        String recipeId = "1";
        String idToDelete = "2";

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(idToDelete);
        recipe.addIngredient(ingredient);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(recipe));

        ingredientService.deleteByRecipeIdAndIngredientId(recipeId, idToDelete).block();

        verify(recipeReactiveRepository).findById(anyString());
        verify(recipeReactiveRepository).save(any(Recipe.class));
    }
}