package com.github.me2nardo.github.connect.util;

import java.util.HashMap;
import java.util.Map;

import static com.github.me2nardo.github.connect.GitHubSourceConnectorConfig.*;
import static com.github.me2nardo.github.connect.GitHubSourceConnectorConfig.TOPIC_CONFIG;

public class TestUtil {

    private TestUtil() { }

    public static Map<String,String> initConfig(){
        Map<String,String> config = new HashMap<>();
        config.put(OWNER_CONFIG,"demoOwner");
        config.put(REPO_CONFIG,"pb");
        config.put(SINCE_CONFIG,"2017-10-23T01:23:22Z");
        config.put(BATCH_SIZE_CONFIG,"100");
        config.put(TOPIC_CONFIG,"github-topic");
        return config;
    }
}
