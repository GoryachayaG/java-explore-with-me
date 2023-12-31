package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilations.CompilationDto;
import ru.practicum.ewm.dto.compilations.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, NewCompilationDto compilationDto);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

}