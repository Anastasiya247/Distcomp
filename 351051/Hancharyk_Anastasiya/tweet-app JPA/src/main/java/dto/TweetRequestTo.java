package com.example.tweetapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class TweetRequestTo {
    @NotNull
    private Long authorId;

    @NotBlank @Size(min = 2, max = 64)
    private String title;

    @NotBlank @Size(min = 4, max = 2048)
    private String content;

    // Используем Set<String>, чтобы принимать как ID (числа в виде строк), так и названия меток
    private Set<String> labelIds;

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Set<String> getLabelIds() { return labelIds; }
    public void setLabelIds(Set<String> labelIds) { this.labelIds = labelIds; }
}