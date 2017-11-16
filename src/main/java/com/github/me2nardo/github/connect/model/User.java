package com.github.me2nardo.github.connect.model;

import org.json.JSONObject;

import static com.github.me2nardo.github.connect.GitHubSchema.*;

public class User {

    private int id;
    private String url;
    private String htmlUrl;
    private String login;

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public static User fromJson(JSONObject jsonObject){
       User user = new User();
       user.setHtmlUrl(jsonObject.getString(USER_HTML_URL_FIELD));
       user.setId(jsonObject.getInt(USER_ID_FIELD));
       user.setLogin(jsonObject.getString(USER_LOGIN_FIELD));
       user.setUrl(jsonObject.getString(USER_URL_FIELD));
       return user;
    }
}
