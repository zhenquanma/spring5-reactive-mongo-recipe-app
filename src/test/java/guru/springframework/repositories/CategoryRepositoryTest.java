package guru.springframework.repositories;

import guru.springframework.models.Category;
import guru.springframework.repositories.reactive.CategotyReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration Test
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryRepositoryTest {

    @Autowired
    CategotyReactiveRepository categotyReactiveRepository;

    @Before
    public void setUp() throws Exception {
        categotyReactiveRepository.deleteAll().block();

        Category category = new Category();
        category.setDescription("French");

        categotyReactiveRepository.save(category).block();
    }

    @Test
    public void testSave() throws Exception {

        Long count = categotyReactiveRepository.count().block();

        assertEquals(Long.valueOf(1L), count);
    }

    @Test
    public void findByDescription() {

        Category french = categotyReactiveRepository.findByDescription("French").block();

        assertNotNull(french.getId());

    }
}