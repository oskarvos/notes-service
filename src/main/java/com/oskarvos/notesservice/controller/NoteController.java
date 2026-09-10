package com.oskarvos.notesservice.controller;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody NoteRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<NoteResponse> getAll(
            @RequestParam(required = false)
            @Size(max = 50, message = "Тег не должен превышать 50 символов")
            String tag,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.getAll(tag, pageable);
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable UUID id,
                               @Valid @RequestBody NoteRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}