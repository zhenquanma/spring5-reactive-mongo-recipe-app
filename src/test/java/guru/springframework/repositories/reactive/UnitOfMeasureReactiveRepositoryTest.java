package guru.springframework.repositories.reactive;

import guru.springframework.models.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {

    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    private final static String foo = "Foo";

    @Before
    public void setUp() throws Exception {
        unitOfMeasureReactiveRepository.deleteAll().block();
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription(foo);
        unitOfMeasureReactiveRepository.save(unitOfMeasure).block();
    }

    @Test
    public void testSave() throws Exception {
        Long count = unitOfMeasureReactiveRepository.count().block();

        assertEquals(Long.valueOf(1L), count);
    }

    @Test
    public void testFindByDescription() throws Exception {

        UnitOfMeasure fooReturn = unitOfMeasureReactiveRepository.findByDescription(foo).block();
        assertEquals(foo, fooReturn.getDescription());
    }
}