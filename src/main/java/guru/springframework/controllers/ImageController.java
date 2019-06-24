package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;

@Controller
public class ImageController {

    private final ImageService imageService;

    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @PostMapping("recipe/{id}/image")
    public String handleImagePost(@PathVariable String id, @RequestParam("imagefile") MultipartFile file) {
        imageService.saveImage(id, file);

        return "redirect:/recipe/" + id + "/show";
    }

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id));

        return "recipe/imageuploadform";
    }

    @GetMapping("recipe/{id}/recipeimage")
    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws Exception {
        RecipeCommand recipeCommand = recipeService.findCommandById(id).block();

        if(recipeCommand.getImage() != null) {
            byte[] bytes = new byte[recipeCommand.getImage().length];

            int i = 0;

            for(byte b : recipeCommand.getImage()) {
                bytes[i++] = b;
            }

            response.setContentType("image/jpeg");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }
}
