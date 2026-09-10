package com.oskarvos.notesservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NoteRequest {

    @NotBlank(message = "Поле 'title' не может быть пустым")
    @Size(max = 200, message = "Поле 'title' не должно превышать 200 символов")
    private String title;

    @Size(max = 10_000, message = "Поле 'content' не должно превышать 10000 символов")
    private String content;

    @Size(max = 20, message = "Не более 20 тегов на заметку")
    private Set<
            @NotBlank(message = "Тег не может быть пустым")
            @Size(max = 50, message = "Тег не должен превышать 50 символов")
                    String
            > tags;
}