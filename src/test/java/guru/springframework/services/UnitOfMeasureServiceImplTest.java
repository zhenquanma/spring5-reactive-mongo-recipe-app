package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.models.UnitOfMeasure;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitOfMeasureServiceImplTest {

    UnitOfMeasureToUnitOfMeasureCommand uomConverter;
    UnitOfMeasureService service;

    @Mock
    UnitOfMeasureReactiveRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        uomConverter = new UnitOfMeasureToUnitOfMeasureCommand();

        service = new UnitOfMeasureServiceImpl(repository, uomConverter);
    }

    @Test
    public void listAll() {
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId("1");

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId("2");

        UnitOfMeasure uom3 = new UnitOfMeasure();
        uom3.setId("3");


        when(repository.findAll()).thenReturn(Flux.just(uom1, uom2, uom3));

        List<UnitOfMeasureCommand> commands = service.listAll().collectList().block();

        assertNotNull(commands);
        assertEquals(3, commands.size());
        verify(repository).findAll();

    }
}