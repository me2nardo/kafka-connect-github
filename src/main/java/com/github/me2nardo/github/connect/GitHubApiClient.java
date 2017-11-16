package com.github.me2nardo.github.connect;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.apache.kafka.connect.errors.ConnectException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GitHubApiClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClient.class);

    private Integer rateLimit = 100;
    private Integer rateRemaining = 100;

    private long rateReset = Instant.MAX.getEpochSecond();
    private GitHubSourceConnectorConfig config;

    public GitHubApiClient(GitHubSourceConnectorConfig config) {
        this.config = config;
    }

    public void sleep() throws InterruptedException {
        long sleepTime = (long) Math.ceil(
                (double) (rateReset - Instant.now().getEpochSecond()) / rateRemaining);
        log.debug(String.format("Sleeping for %s seconds", sleepTime ));
        Thread.sleep(1000 * sleepTime);
    }

    public void sleepIfNeed() throws InterruptedException {

        if (rateRemaining <= 10 && rateRemaining > 0) {
            log.info(String.format("Approaching limit soon, you have %s requests left", rateRemaining));
            sleep();
        }
    }


    protected String constructUrl(Integer page, Instant since){
        return String.format(
                "https://api.github.com/repos/%s/%s/issues?page=%s&per_page=%s&since=%s&state=all&direction=asc&sort=updated",
                config.getOwnerConfig(),
                config.getRepoConfig(),
                page,
                config.getBatchSize(),
                since.toString());
    }

    protected JSONArray getNextIssues(Integer page, Instant since) throws InterruptedException {

        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse = getNextIssuesAPI(page, since);


            Headers headers = jsonResponse.getHeaders();
            rateLimit = Integer.valueOf(headers.getFirst("X-RateLimit-Limit"));
            rateRemaining = Integer.valueOf(headers.getFirst("X-RateLimit-Remaining"));
            rateReset = Integer.valueOf(headers.getFirst("X-RateLimit-Reset"));
            switch (jsonResponse.getStatus()){
                case 200:
                    return jsonResponse.getBody().getArray();
                case 401:
                    throw new ConnectException("Bad GitHub credentials provided, please edit your config");
                case 403:

                    log.info(jsonResponse.getBody().getObject().getString("message"));
                    log.info(String.format("Your rate limit is %s", rateLimit));
                    log.info(String.format("Your remaining calls is %s", rateRemaining));
                    log.info(String.format("The limit will reset at %s",
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(rateReset), ZoneOffset.systemDefault())));
                    long sleepTime = rateReset - Instant.now().getEpochSecond();
                    log.info(String.format("Sleeping for %s seconds", sleepTime ));
                    Thread.sleep(1000 * sleepTime);
                    return getNextIssues(page, since);
                default:
                    log.error(constructUrl(page, since));
                    log.error(String.valueOf(jsonResponse.getStatus()));
                    log.error(jsonResponse.getBody().toString());
                    log.error(jsonResponse.getHeaders().toString());
                    log.error("Unknown error: Sleeping 5 seconds " +
                            "before re-trying");
                    Thread.sleep(5000L);
                    return getNextIssues(page, since);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return new JSONArray();
        }
    }

    protected HttpResponse<JsonNode> getNextIssuesAPI(Integer page, Instant since) throws UnirestException {
        GetRequest unirest = Unirest.get(constructUrl(page, since));
        if (!config.getAuthUsername().isEmpty() && !config.getAuthPassword().isEmpty() ){
            unirest = unirest.basicAuth(config.getAuthUsername(), config.getAuthPassword());
        }
        log.debug(String.format("GET %s", unirest.getUrl()));
        return unirest.asJson();
    }

}
