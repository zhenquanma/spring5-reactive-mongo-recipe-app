package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureConverter;

    public UnitOfMeasureServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureConverter) {
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.unitOfMeasureConverter = unitOfMeasureConverter;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAll() {

        return unitOfMeasureReactiveRepository
                .findAll()
                .map(unitOfMeasureConverter :: convert);
    }
}
