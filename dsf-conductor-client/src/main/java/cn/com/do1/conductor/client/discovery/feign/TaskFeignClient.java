package cn.com.do1.conductor.client.discovery.feign;

import cn.com.do1.conductor.client.discovery.feign.hystrix.TaskFeignClientHystrix;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengxc
 */
@FeignClient(name = "conductor", contextId = "Conductor.TaskFeignClient", url = "${conductor.client.rootUri:}", fallback = TaskFeignClientHystrix.class)
public interface TaskFeignClient {

    String PREFIX_PATH = "/api/tasks";

    @GetMapping(PREFIX_PATH + "/poll/{taskType}")
    Task pollTask(@PathVariable("taskType") String taskType, @RequestParam("workerid") String workerId, @RequestParam("domain") String domain);

    @PostMapping(value = PREFIX_PATH + "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateTask(@RequestBody TaskResult result);
}
