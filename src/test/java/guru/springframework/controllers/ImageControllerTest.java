package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Ignore
public class ImageControllerTest {

    @Mock
    ImageService imageService;

    @Mock
    RecipeService recipeService;

    ImageController controller;

    MockMvc mockMvc;

    private static final String COMMAND_ID = "1";


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new ImageController(imageService, recipeService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getImageForm() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId(COMMAND_ID);

        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

        //when
        mockMvc.perform(get("/recipe/1/image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"));

        verify(recipeService).findCommandById(anyString());

    }

    @Test
    public void handleImagePost() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("imagefile", "testing.txt", "text/plain",
                        "Spring".getBytes());

        when(imageService.saveImage(anyString(),any())).thenReturn(Mono.empty());

        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/recipe/1/show"));

        verify(imageService).saveImage(anyString(), any());
    }

    @Test
    public void renderImageFromDB() throws Exception {
//        RecipeCommand recipeCommand = new RecipeCommand();
//        recipeCommand.setId(COMMAND_ID);
//
//        String str = "an image";
//        Byte[] bytes = new Byte[str.getBytes().length];
//
//        int i = 0;
//
//        for(byte b : str.getBytes()){
//            bytes[i++] = b;
//        }
//
//        recipeCommand.setImage(bytes);
//
//        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));
//
//        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//
//        byte[] byteArray = response.getContentAsByteArray();
//
//        assertEquals(str.getBytes().length, byteArray.length);
    }
}