package cn.com.do1.conductor.client.discovery.feign.hystrix;

import cn.com.do1.conductor.client.discovery.feign.TaskFeignClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.stereotype.Component;

/**
 * @author zengxc
 */
@Component
public class TaskFeignClientHystrix implements TaskFeignClient {
    @Override
    public Task pollTask(String taskType, String workerId, String domain) {
        return new Task();
    }

    @Override
    public void updateTask(TaskResult result) {

    }
}
