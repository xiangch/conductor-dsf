package cn.com.do1.conductor.client.discovery.feign.hystrix;

import cn.com.do1.conductor.client.discovery.feign.WorkflowFeignClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import org.springframework.stereotype.Component;

/**
 * @author zengxc
 */
@Component
public class WorkflowFeignClientHystrix implements WorkflowFeignClient {
    @Override
    public String startWorkflow(StartWorkflowRequest startWorkflowRequest) {
        return null;
    }

    @Override
    public Workflow getWorkflow(String workflowId, boolean includeTasks) {
        return new Workflow();
    }
}
