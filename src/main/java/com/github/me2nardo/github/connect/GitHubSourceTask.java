package com.github.me2nardo.github.connect;

import com.github.me2nardo.github.connect.model.Issue;
import com.github.me2nardo.github.connect.model.User;
import com.github.me2nardo.github.connect.utils.DateUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.me2nardo.github.connect.GitHubSchema.*;

public class GitHubSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(GitHubSourceTask.class);
    public GitHubSourceConnectorConfig config;

    protected Instant nextQuerySince;
    protected Integer lastIssueNumber;
    protected Integer nextPageToVisit = 1;
    protected Instant lastUpdatedAt;

    public GitHubApiClient gitHubHttpAPIClient;

    @Override
    public void start(Map<String, String> map) {
       config = new GitHubSourceConnectorConfig(map);
       initLastVariable();
       gitHubHttpAPIClient = new GitHubApiClient(config);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        gitHubHttpAPIClient.sleepIfNeed();

        final List<SourceRecord> records = new ArrayList<>();
        JSONArray issues = gitHubHttpAPIClient.getNextIssues(nextPageToVisit,nextQuerySince);

        int i = 0;
        for (Object obj : issues) {
            Issue issue = Issue.fromJson((JSONObject) obj);
            SourceRecord sourceRecord = generateSourceRecord(issue);
            records.add(sourceRecord);
            i += 1;
            lastUpdatedAt = issue.getUpdatedAt();
        }

        if (i == 100){
            // we have reached a full batch, we need to get the next one
            nextPageToVisit += 1;
        }
        else {
            nextQuerySince = lastUpdatedAt.plusSeconds(1);
            nextPageToVisit = 1;
            gitHubHttpAPIClient.sleep();
        }
        return records;
    }

    @Override
    public void stop() {

    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    private void initLastVariable(){
        Map<String,Object> lastOffset = null;
        lastOffset = context.offsetStorageReader().offset(sourcePartition());
        if (lastOffset==null){
            // don't fetch any record yet
            nextQuerySince = config.getSince();
            lastIssueNumber = -1;
        } else {
            Object updatedAt = lastOffset.get(UPDATED_AT_FIELD);
            Object id = lastOffset.get(NUMBER_FIELD);
            Object nextPage = lastOffset.get(NEXT_PAGE_FIELD);

            if (updatedAt!=null && (updatedAt instanceof String)){
                nextQuerySince = Instant.parse((String)updatedAt);
            }

            if (id!=null && (id instanceof String)){
                lastIssueNumber = Integer.valueOf((String)id);
            }

            if (nextPage!=null & (nextPage instanceof String)){
                nextPageToVisit = Integer.valueOf((String)nextPage);
            }
        }
    }

    private Map<String,String> sourcePartition(){
        Map<String, String> map = new HashMap<>();
        map.put(OWNER_FIELD, config.getOwnerConfig());
        map.put(REPOSITORY_FIELD, config.getRepoConfig());
        return map;
    }


    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, DateUtils.MaxInstant(updatedAt, nextQuerySince).toString());
        map.put(NEXT_PAGE_FIELD, nextPageToVisit.toString());
        return map;
    }

    private SourceRecord generateSourceRecord(Issue issue) {
        return new SourceRecord(
                sourcePartition(),
                sourceOffset(issue.getUpdatedAt()),
                config.getTopic(),
                null, // partition will be inferred by the framework
                KEY_SCHEMA,
                buildRecordKey(issue),
                VALUE_SCHEMA,
                buildRecordValue(issue),
                issue.getUpdatedAt().toEpochMilli());
    }

    private Struct buildRecordKey(Issue issue){
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put(OWNER_FIELD, config.getOwnerConfig())
                .put(REPOSITORY_FIELD, config.getRepoConfig())
                .put(NUMBER_FIELD, issue.getId());

        return key;
    }

    private Struct buildRecordValue(Issue issue){

        // Issue top level fields
        Struct valueStruct = new Struct(VALUE_SCHEMA)
                .put(URL_FIELD, issue.getUrl())
                .put(TITLE_FIELD, issue.getTitle())
                .put(CREATED_AT_FIELD, issue.getCreatedAt().toEpochMilli())
                .put(UPDATED_AT_FIELD, issue.getUpdatedAt().toEpochMilli())
                .put(NUMBER_FIELD, issue.getId())
                .put(STATE_FIELD, issue.getState());

        // User is mandatory
        User user = issue.getUser();
        Struct userStruct = new Struct(USER_SCHEMA)
                .put(USER_URL_FIELD, user.getUrl())
                .put(USER_ID_FIELD, user.getId())
                .put(USER_LOGIN_FIELD, user.getLogin());
        valueStruct.put(USER_FIELD, userStruct);

        return valueStruct;
    }

}
