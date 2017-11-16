package com.github.me2nardo.github.connect;

import com.github.me2nardo.github.connect.util.TestUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitHubSourceConnectorTest {

    @Test
    public void taskGitHubSourceConnector(){
        GitHubSourceConnector gitHubSourceConnector = new GitHubSourceConnector();
        gitHubSourceConnector.start(TestUtil.initConfig());
        assertEquals(gitHubSourceConnector.taskConfigs(1).size(),1);
        assertEquals(gitHubSourceConnector.taskConfigs(100).size(),1);
    }
}
