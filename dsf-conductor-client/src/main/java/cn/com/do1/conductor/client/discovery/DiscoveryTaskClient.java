package cn.com.do1.conductor.client.discovery;

import cn.com.do1.conductor.client.discovery.feign.TaskFeignClient;
import com.google.common.base.Preconditions;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author zengxc
 */

public class DiscoveryTaskClient extends TaskClient {

    private TaskFeignClient feign;


    public DiscoveryTaskClient(TaskFeignClient feign) {
        super();
        this.feign = feign;
    }

    @Override
    public Task pollTask(String taskType, String workerId, String domain) {
        Preconditions.checkArgument(!StringUtils.isEmpty(taskType), "Task type cannot be blank");
        Preconditions.checkArgument(!StringUtils.isEmpty(workerId), "Worker id cannot be blank");
        Task task = Optional.ofNullable(
            feign.pollTask(taskType, workerId, domain)
        ).orElse(new Task());
        return task;
    }

    @Override
    public void updateTask(TaskResult taskResult) {
        Preconditions.checkNotNull(taskResult, "Task result cannot be null");
        feign.updateTask(taskResult);
    }


}
