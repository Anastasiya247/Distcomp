package com.example.tweetapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_reaction")
public class Reaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(nullable = false, length = 2048)
    private String content;

    public Tweet getTweet() { return tweet; }
    public void setTweet(Tweet tweet) { this.tweet = tweet; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}