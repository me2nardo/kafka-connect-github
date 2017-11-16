package com.github.me2nardo.github.connect;

import com.github.me2nardo.github.connect.util.TestUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.me2nardo.github.connect.GitHubSourceConnectorConfig.*;

public class GitHubSourceConnectorConfigTest {

    private ConfigDef configDef = GitHubSourceConnectorConfig.conf();

    @Test
    public void doc(){
        System.out.println(GitHubSourceConnectorConfig.conf().toRst());
    }

    @Test
    public void initConfigIsValid(){
        assert(configDef.validate(TestUtil.initConfig()).stream().allMatch(value->value.errorMessages().size()==0));
    }

    @Test
    public void canReadConfigCorrectly() {
        GitHubSourceConnectorConfig config = new GitHubSourceConnectorConfig(TestUtil.initConfig());
        config.getAuthPassword();

    }


    @Test
    public void validateSince() {
        Map<String, String> config = TestUtil.initConfig();
        config.put(SINCE_CONFIG, "not-a-date");
        ConfigValue configValue = configDef.validateAll(config).get(SINCE_CONFIG);
        assert (configValue.errorMessages().size() > 0);
    }

    @Test
    public void validateBatchSize() {
        Map<String, String> config = TestUtil.initConfig();
        config.put(BATCH_SIZE_CONFIG, "-1");
        ConfigValue configValue = configDef.validateAll(config).get(BATCH_SIZE_CONFIG);
        assert (configValue.errorMessages().size() > 0);

        config = TestUtil.initConfig();
        config.put(BATCH_SIZE_CONFIG, "101");
        configValue = configDef.validateAll(config).get(BATCH_SIZE_CONFIG);
        assert (configValue.errorMessages().size() > 0);

    }

    @Test
    public void validateUsername() {
        Map<String, String> config = TestUtil.initConfig();
        config.put(AUTH_USERNAME_CONFIG, "username");
        ConfigValue configValue = configDef.validateAll(config).get(AUTH_USERNAME_CONFIG);
        assert (configValue.errorMessages().size() == 0);
    }

    @Test
    public void validatePassword() {
        Map<String, String> config = TestUtil.initConfig();
        config.put(AUTH_PASSWORD_CONFIG, "password");
        ConfigValue configValue = configDef.validateAll(config).get(AUTH_PASSWORD_CONFIG);
        assert (configValue.errorMessages().size() == 0);
    }

}
