package com.learnreactivespring.legacyapi.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Todo{
    private int id;
    private String title;
    private boolean completed;
}