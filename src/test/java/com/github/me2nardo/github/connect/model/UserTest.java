package com.github.me2nardo.github.connect.model;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private String userJson = "{\n" +
            "\"login\": \"simplesteph\",\n" +
            "\"id\": 20851561,\n" +
            "\"avatar_url\": \"https://avatars3.githubusercontent.com/u/20851561?v=3\",\n" +
            "\"gravatar_id\": \"\",\n" +
            "\"url\": \"https://api.github.com/users/simplesteph\",\n" +
            "\"html_url\": \"https://github.com/simplesteph\",\n" +
            "\"followers_url\": \"https://api.github.com/users/simplesteph/followers\",\n" +
            "\"following_url\": \"https://api.github.com/users/simplesteph/following{/other_user}\",\n" +
            "\"gists_url\": \"https://api.github.com/users/simplesteph/gists{/gist_id}\",\n" +
            "\"starred_url\": \"https://api.github.com/users/simplesteph/starred{/owner}{/repo}\",\n" +
            "\"subscriptions_url\": \"https://api.github.com/users/simplesteph/subscriptions\",\n" +
            "\"organizations_url\": \"https://api.github.com/users/simplesteph/orgs\",\n" +
            "\"repos_url\": \"https://api.github.com/users/simplesteph/repos\",\n" +
            "\"events_url\": \"https://api.github.com/users/simplesteph/events{/privacy}\",\n" +
            "\"received_events_url\": \"https://api.github.com/users/simplesteph/received_events\",\n" +
            "\"type\": \"User\",\n" +
            "\"site_admin\": false\n" +
            "}";

    private JSONObject jsonObject = new JSONObject(userJson);

    @Test
    public void testParseUser(){
        User user =  User.fromJson(jsonObject);
        assertEquals(user.getId(),20851561);
    }
}
