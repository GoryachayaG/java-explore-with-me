package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.categories.CategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        Category savedCategory = repository.save(category);
        log.info("Создана категория {}", savedCategory);
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        repository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
        List<Event> eventsWithThatCategory = eventRepository.findAllByCategoryId(catId);
        if (eventsWithThatCategory.size() != 0) {
            throw new ConflictException("The category is not empty");
        }
        repository.deleteById(catId);
        log.info("Удалена категория id = {}", catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category categoryFromDB = repository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
        categoryFromDB.setName(categoryDto.getName());
        log.info("Обновлена категория id = {}", catId);
        return CategoryMapper.toCategoryDto(repository.save(categoryFromDB));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> categories = repository.findAll(page).getContent();
        log.info("Получили список категорий");
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        Category categoryFromDB = repository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
        log.info("Получили категорию по id = {}", catId);
        return CategoryMapper.toCategoryDto(categoryFromDB);
    }
}