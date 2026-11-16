package com.example.tweetapp.model;

public class Reaction extends BaseEntity {
    private Long tweetId;
    private String content;

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
