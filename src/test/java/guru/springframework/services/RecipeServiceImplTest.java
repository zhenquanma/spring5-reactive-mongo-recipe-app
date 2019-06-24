package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.models.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


/**
 * Using Mockito mocks
 */
public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeReactiveRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipeByIdTest() throws Exception{
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        Recipe returnedRecipe = recipeService.findById("1").block();

        assertNotNull("Null recipe returned", returnedRecipe);

        verify(recipeRepository).findById(anyString());

        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void getRecipesTest() throws Exception {

        Recipe recipe = new Recipe();

        when(recipeRepository.findAll()).thenReturn(Flux.just(recipe));

        List<Recipe> recipes = recipeService.getRecipes().collectList().block();

        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
        verify(recipeRepository, never()).findById(anyString());
    }

    @Test
    public void testDelete() throws Exception {
        String idToDelete = "2";

        when(recipeRepository.deleteById(anyString())).thenReturn(Mono.empty());

        recipeService.deleteById(idToDelete).block();

        verify(recipeRepository).deleteById(anyString());
    }

    @Test(expected = NotFoundException.class)
    public void testGetRecipeNotFound() throws Exception {

        when(recipeRepository.findById(anyString())).thenReturn(Mono.empty());

        Recipe recipe = recipeService.findById("1").block();
    }
}