package cn.com.do1.conductor.client.discovery.feign;

import cn.com.do1.conductor.client.discovery.feign.hystrix.WorkflowFeignClientHystrix;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengxc
 */
@FeignClient(name = "conductor", contextId = "Conductor.WorkflowFeignClient", url = "${conductor.client.rootUri:}", fallback = WorkflowFeignClientHystrix.class)
public interface WorkflowFeignClient {
    String PREFIX_PATH = "/api/workflow";

    @PostMapping(value = PREFIX_PATH + "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    String startWorkflow(@RequestBody StartWorkflowRequest startWorkflowRequest);

    @GetMapping(PREFIX_PATH + "/{workflowId}")
    Workflow getWorkflow(@PathVariable("workflowId") String workflowId, @RequestParam("includeTasks") boolean includeTasks);

}
