package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Slf4j
@Controller
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }


    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredients for recipe id: " + recipeId);

        model.addAttribute("recipe", recipeService.findCommandById(recipeId).block());

        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredients/{id}/show")
    public String showIngredient(@PathVariable String recipeId, @PathVariable String id, Model model){
        log.debug("Getting an ingredient with id: " + id + " for recipe id: " + recipeId);

        model.addAttribute("ingredient",
                ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());

        return "recipe/ingredient/show";
    }


    @GetMapping("recipe/{recipeId}/ingredients/new")
    public String newIngredient(@PathVariable String recipeId, Model model) {

        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();

        if(recipeCommand == null){
            throw new RuntimeException("RECIPE NOT FOUND: " + recipeId);
        }

        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(UUID.randomUUID().toString());
        ingredientCommand.setRecipeId(recipeId);
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("ingredient", ingredientCommand);
        model.addAttribute("uomList", unitOfMeasureService.listAll().collectList().block());

        return "recipe/ingredient/ingredientform";
    }

    /**
     * API for the view of updating a recipe ingredient
     * @param recipeId
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/recipe/{recipeId}/ingredients/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId, @PathVariable String id, Model model){
        model.addAttribute("ingredient",
                ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());

        model.addAttribute("uomList", unitOfMeasureService.listAll().collectList().block());

        return "recipe/ingredient/ingredientform";
    }


    /**
     * API for saving the change of a recipe ingredient
     * @param command
     * @return
     */
    @PostMapping("/recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command) {
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

        log.debug("Save ingredient with id: " + savedCommand.getId() + " to recipe id: " + savedCommand.getRecipeId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredients/" + savedCommand.getId() + "/show";
    }


    @GetMapping("/recipe/{recipeId}/ingredients/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId, @PathVariable String id) {

        log.debug("Deleting ingredient: " + id + " of recipe: " + recipeId);

        ingredientService.deleteByRecipeIdAndIngredientId(recipeId, id).block();

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
