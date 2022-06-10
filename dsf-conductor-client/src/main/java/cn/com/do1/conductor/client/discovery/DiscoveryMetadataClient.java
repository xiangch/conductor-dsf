package cn.com.do1.conductor.client.discovery;


import cn.com.do1.conductor.client.discovery.feign.MetadataFeignClient;
import cn.com.do1.conductor.client.discovery.feign.WorkflowFeignClient;
import com.google.common.base.Preconditions;
import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.telemetry.MetricsContainer;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengxc
 */
@Slf4j
public class DiscoveryMetadataClient {
    private MetadataFeignClient feign;


    public DiscoveryMetadataClient(MetadataFeignClient feign) {
        this.feign = feign;
    }

    public void unregisterWorkflowDef(String name, Integer version) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "Workflow name cannot be blank");
        Preconditions.checkNotNull(version, "Version cannot be null");
        feign.unregisterWorkflowDef(name, version);
    }

    public void registerWorkflowDef(WorkflowDef workflowDef) {
        Preconditions.checkNotNull(workflowDef, "Workflow definition cannot be null");
        feign.registerWorkflowDef(workflowDef);
    }
}
