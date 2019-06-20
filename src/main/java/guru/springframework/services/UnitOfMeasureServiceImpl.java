package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    UnitOfMeasureRepository unitOfMeasureRepository;
    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureConverter;

    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository unitOfMeasureRepository,
                                    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureConverter) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.unitOfMeasureConverter = unitOfMeasureConverter;
    }

    @Override
    public Set<UnitOfMeasureCommand> listAll() {

        return stream(unitOfMeasureRepository.findAll().spliterator(), false)
                .map(unitOfMeasureConverter :: convert)
                .collect(Collectors.toSet());
    }
}
