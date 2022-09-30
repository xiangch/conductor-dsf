package cn.com.do1.conductor.client.discovery.feign.hystrix;

import cn.com.do1.conductor.client.discovery.feign.MetadataFeignClient;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import org.springframework.stereotype.Component;

/**
 * @author zengxc
 */
@Component
public class MetadataFeignClientHystrix implements MetadataFeignClient {
    @Override
    public void registerWorkflowDef(WorkflowDef workflowDef) {

    }

    @Override
    public void unregisterWorkflowDef(String name, Integer version) {

    }

    @Override
    public WorkflowDef getWorkflowDef(String name, Integer version) {        
        return null;
    }
}
