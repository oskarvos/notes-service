package com.oskarvos.notesservice.repository;

import com.oskarvos.notesservice.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Override
    @EntityGraph(attributePaths = "tags")
    Optional<Note> findById(UUID id);

    @Query("SELECT n FROM Note n WHERE :tag MEMBER OF n.tags")
    Page<Note> findByTag(@Param("tag") String tag, Pageable pageable);
}