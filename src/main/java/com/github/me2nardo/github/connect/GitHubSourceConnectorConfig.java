package com.github.me2nardo.github.connect;

import com.github.me2nardo.github.connect.validator.BatchSizeValidator;
import com.github.me2nardo.github.connect.validator.TimeStampValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

public class GitHubSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG="topic";
    public static final String TOPIC_DOC = "topic to write";

    public static final String OWNER_CONFIG="github.owner";
    public static final String OWNER_DOC = "Owner repository";

    public static final String REPO_CONFIG = "github.repo";
    private static final String REPO_DOC = "Repository you'd like to follow";

    public static final String SINCE_CONFIG = "since.timestamp";
    private static final String SINCE_DOC =
            "Only issues updated at or after this time are returned.\n"
                    + "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.\n"
                    + "Defaults to a year from first launch.";

    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

    public static final String AUTH_USERNAME_CONFIG = "auth.username";
    private static final String AUTH_USERNAME_DOC = "Optional Username to authenticate calls";

    public static final String AUTH_PASSWORD_CONFIG = "auth.password";
    private static final String AUTH_PASSWORD_DOC = "Optional Password to authenticate calls";

    public GitHubSourceConnectorConfig(ConfigDef configDef,Map<String,String> parseConfig){
        super(configDef,parseConfig);
    }

    public GitHubSourceConnectorConfig(Map<String,String> parseConfig){
        this(conf(),parseConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, TOPIC_DOC)
                .define(OWNER_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, OWNER_DOC)
                .define(REPO_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, REPO_DOC)
                .define(BATCH_SIZE_CONFIG, ConfigDef.Type.INT, 100, new BatchSizeValidator(), ConfigDef.Importance.LOW, BATCH_SIZE_DOC)
                .define(SINCE_CONFIG, ConfigDef.Type.STRING, ZonedDateTime.now().minusYears(1).toInstant().toString(),
                        new TimeStampValidator(), ConfigDef.Importance.HIGH, SINCE_DOC)
                .define(AUTH_USERNAME_CONFIG, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH, AUTH_USERNAME_DOC)
                .define(AUTH_PASSWORD_CONFIG, ConfigDef.Type.PASSWORD, "", ConfigDef.Importance.HIGH, AUTH_PASSWORD_DOC);
    }

    public String getOwnerConfig() {
        return this.getString(OWNER_CONFIG);
    }

    public String getRepoConfig() {
        return this.getString(REPO_CONFIG);
    }

    public Integer getBatchSize() {
        return this.getInt(BATCH_SIZE_CONFIG);
    }

    public Instant getSince() {
        return Instant.parse(this.getString(SINCE_CONFIG));
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }

    public String getAuthUsername() {
        return this.getString(AUTH_USERNAME_CONFIG);
    }

    public String getAuthPassword(){
        return this.getPassword(AUTH_PASSWORD_CONFIG).value();
    }
}
