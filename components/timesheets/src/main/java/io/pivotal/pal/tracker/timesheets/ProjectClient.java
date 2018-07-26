package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ConcurrentHashMap<Long, ProjectInfo> infoCache = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return infoCache.get(projectId);
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        logger.info("Getting project with id {} from end point {} - Cache miss", projectId, endpoint);
        ProjectInfo info = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        infoCache.put(projectId, info);
        return info;
    }
}
