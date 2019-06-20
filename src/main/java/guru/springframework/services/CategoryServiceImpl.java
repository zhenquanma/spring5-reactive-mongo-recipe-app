package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.converters.CategoryToCategoryCommand;
import guru.springframework.models.Category;
import guru.springframework.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryToCategoryCommand categoryConverter;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryToCategoryCommand categoryConverter) {
        this.categoryRepository = categoryRepository;
        this.categoryConverter = categoryConverter;
    }

    @Override
    public CategoryCommand findCommandById(String id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(!categoryOptional.isPresent()) {
            throw new RuntimeException("CATEGORY NOT FOUND");
        }
        Category category = categoryOptional.get();
        return categoryConverter.convert(category);
    }

    @Override
    public Set<CategoryCommand> findAll() {
        return stream(categoryRepository.findAll().spliterator(), false)
                .map(categoryConverter::convert)
                .collect(Collectors.toSet());
    }
}
