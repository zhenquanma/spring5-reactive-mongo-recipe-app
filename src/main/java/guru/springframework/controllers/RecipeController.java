package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.CategoryService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/recipe")
public class RecipeController {

    private RecipeService recipeService;
    private CategoryService categoryService;

    private WebDataBinder webDataBinder;

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";

    public RecipeController(RecipeService recipeService, CategoryService categoryService) {
        this.recipeService = recipeService;
        this.categoryService = categoryService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        this.webDataBinder = webDataBinder;
    }


    @GetMapping("/{id}/show")
    public String showById(@PathVariable String id, Model model){

        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/show";
    }

    @GetMapping("/new")
    public String newRecipe(Model model) {

        model.addAttribute("recipe", new RecipeCommand());
        //All categories in database
        model.addAttribute("categoryList", categoryService.findAll());

        //Categories in the found recipe
        model.addAttribute("categoryIds", new ArrayList<>());

        return RECIPE_RECIPEFORM_URL;
    }

    @GetMapping("/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
//        Mono<RecipeCommand> recipeCommandMono = recipeService.findCommandById(id);
        RecipeCommand recipe = recipeService.findCommandById(id).block();
        List<String> categoryIds = new ArrayList<>();
        recipe.getCategories().iterator().forEachRemaining(category -> categoryIds.add(category.getId()));
        model.addAttribute("recipe", recipe);

        //All categories in database
        model.addAttribute("categoryList", categoryService.findAll());

        //Categories in the found recipe
        model.addAttribute("categoryIds", categoryIds);

        return RECIPE_RECIPEFORM_URL;
    }

    @PostMapping({"/",""})
    public String saveOrUpdate(@ModelAttribute("recipe") RecipeCommand command,
                               @RequestParam("categoryId") String[] categoryId) {

        webDataBinder.validate();
        BindingResult bindingResult = webDataBinder.getBindingResult();

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors()
                    .forEach(e -> log.debug(e.toString()));
            return RECIPE_RECIPEFORM_URL;
        }

        for(int i = 0; i < categoryId.length; i++){
            command.getCategories().add(categoryService.findCommandById(categoryId[i]));
        }
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command).block();

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("/{id}/delete")
    public String deleteCommand(@PathVariable String id) {
        log.debug("Deleting Recipe ID: " + id);

        recipeService.deleteById(id).block();

        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String notFoundHandler(Exception e, Model model) {
        log.error("Handling not found exception");
        log.error(e.getMessage());

        model.addAttribute("exception", e);

        return "404error";
    }

}
