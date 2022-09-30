package cn.com.do1.conductor.client.discovery;

import cn.com.do1.conductor.client.discovery.feign.WorkflowFeignClient;
import com.google.common.base.Preconditions;
import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.telemetry.MetricsContainer;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengxc
 */
@Slf4j
public class DiscoveryWorkflowClient {
    
    private WorkflowFeignClient feign;

    public DiscoveryWorkflowClient(WorkflowFeignClient feign) {
        this.feign = feign;
    }

    public String startWorkflow(StartWorkflowRequest startWorkflowRequest) {
        Preconditions.checkNotNull(startWorkflowRequest, "StartWorkflowRequest cannot be null");
        Preconditions.checkArgument(
                StringUtils.isNotBlank(startWorkflowRequest.getName()),
                "Workflow name cannot be null or empty");
        Preconditions.checkArgument(
                StringUtils.isBlank(startWorkflowRequest.getExternalInputPayloadStoragePath()),
                "External Storage Path must not be set");

        String version = startWorkflowRequest.getVersion() != null
                ? startWorkflowRequest.getVersion().toString()
                : "latest";
        try {
            return feign.startWorkflow(startWorkflowRequest);
        } catch (ConductorClientException e) {
            String errorMsg = String.format(
                    "Unable to send start workflow request:%s, version:%s",
                    startWorkflowRequest.getName(), version);
            log.error(errorMsg, e);
            MetricsContainer.incrementWorkflowStartErrorCount(startWorkflowRequest.getName(), e);
            throw e;
        }
    }

    public Workflow getWorkflow(String workflowId, boolean includeTasks) {
        Preconditions.checkArgument(
                StringUtils.isNotBlank(workflowId), "workflow id cannot be blank");
        Workflow workflow = feign.getWorkflow(workflowId, includeTasks);
        return workflow;
    }

    /**
     * Paginated search for workflows based on payload
     *
     * @param start    start value of page
     * @param size     number of workflows to be returned
     * @param sort     sort order
     * @param freeText additional free text query
     * @param query    the search query
     * @return the {@link SearchResult} containing the {@link WorkflowSummary} that
     *         match the query
     */
    public SearchResult<WorkflowSummary> search(
            Integer start, Integer size, String sort, String freeText, String query) {
        return feign.search(start, size, sort, freeText, query);
    }

}
