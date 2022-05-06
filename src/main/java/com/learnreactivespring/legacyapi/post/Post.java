package com.learnreactivespring.legacyapi.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Post {

    private int id;
    private int userId;
    private String title;
    private String body;

}
