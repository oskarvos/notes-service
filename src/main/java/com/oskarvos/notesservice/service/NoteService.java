package com.oskarvos.notesservice.service;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NoteService {

    NoteResponse create(NoteRequest request);

    NoteResponse getById(UUID id);

    Page<NoteResponse> getAll(String tag, Pageable pageable);

    NoteResponse update(UUID id, NoteRequest request);

    void delete(UUID id);
}