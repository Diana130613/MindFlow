package ru.mindflow.backend.mediator;

import ru.mindflow.backend.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();
    CategoryDto findById(Long id);
}