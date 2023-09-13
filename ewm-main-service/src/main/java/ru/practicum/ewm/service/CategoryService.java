package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.categories.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategoryById(Long id);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);
}