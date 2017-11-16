package com.github.me2nardo.github.connect.model;

import org.json.JSONObject;

import java.time.Instant;

import static com.github.me2nardo.github.connect.GitHubSchema.*;

public class Issue {

    private int id;
    private String url;
    private String htmlUrl;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    private String state;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Issue fromJson(JSONObject jsonObject){
        Issue issue = new Issue();
        issue.setTitle(jsonObject.getString(TITLE_FIELD));
        issue.setUrl(jsonObject.getString(URL_FIELD));
        issue.setId(jsonObject.getInt(NUMBER_FIELD));
        issue.setCreatedAt(Instant.parse(jsonObject.getString(CREATED_AT_FIELD)));
        issue.setUpdatedAt(Instant.parse(jsonObject.getString(UPDATED_AT_FIELD)));
        issue.setState(jsonObject.getString(STATE_FIELD));

        User user = User.fromJson(jsonObject.getJSONObject(USER_FIELD));

        issue.setUser(user);

        return issue;
    }
}
