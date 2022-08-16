package cn.com.do1.conductor.client.discovery.feign.hystrix;

import cn.com.do1.conductor.client.discovery.feign.WorkflowFeignClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
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

    @Override
    public SearchResult<WorkflowSummary> search(int start, int size, String sort, String freeText, String query) {
        return null;
    }
}
