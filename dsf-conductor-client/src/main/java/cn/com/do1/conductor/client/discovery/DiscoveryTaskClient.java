package cn.com.do1.conductor.client.discovery;

import cn.com.do1.conductor.client.spring.ClientProperties;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * @author zengxc
 */

public class DiscoveryTaskClient extends TaskClient {

    private RestTemplate restTemplate;
    private String rootUri;

    public DiscoveryTaskClient(RestTemplate restTemplate,
                               String rootUri) {
        super();
        super.setRootURI(rootUri);
        this.restTemplate = restTemplate;
        this.rootUri = rootUri;

    }

    @Override
    public Task pollTask(String taskType, String workerId, String domain) {
        Preconditions.checkArgument(!StringUtils.isEmpty(taskType), "Task type cannot be blank");
        Preconditions.checkArgument(!StringUtils.isEmpty(workerId), "Worker id cannot be blank");
        Task task = Optional.ofNullable(
                getForTask(taskType, workerId, domain)
        ).orElse(new Task());
        return task;
    }

    @Override
    public void updateTask(TaskResult taskResult) {
        Preconditions.checkNotNull(taskResult, "Task result cannot be null");
        restTemplate.postForObject(rootUri + "tasks", taskResult, String.class);
    }


    private Task getForTask(String taskType, String workerId, String domain) {
        String url = String.format(rootUri + "tasks/poll/%s?workerid=%s", taskType, workerId);
        if (!StringUtils.isEmpty(domain)) {
            url += "&domain=" + domain;
        }
        String response = restTemplate.getForObject(url, String.class);
        return JSONObject.parseObject(response, Task.class);
    }


}
