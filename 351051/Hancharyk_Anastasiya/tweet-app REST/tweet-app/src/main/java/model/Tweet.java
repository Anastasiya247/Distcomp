package com.example.tweetapp.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Tweet extends BaseEntity {
    private Long authorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
    private Set<Long> labelIds = new HashSet<>();

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }
    public Instant getModified() { return modified; }
    public void setModified(Instant modified) { this.modified = modified; }
    public Set<Long> getLabelIds() { return labelIds; }
    public void setLabelIds(Set<Long> labelIds) { this.labelIds = labelIds; }
}
