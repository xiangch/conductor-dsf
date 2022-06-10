package cn.com.do1.conductor.client.discovery.feign;

import cn.com.do1.conductor.client.discovery.feign.hystrix.MetadataFeignClientHystrix;
import cn.com.do1.conductor.client.discovery.feign.hystrix.TaskFeignClientHystrix;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zengxc
 */
@FeignClient(name = "conductor", contextId = "MetadataFeignClient", url = "${conductor.client.rootUri:}", fallback = MetadataFeignClientHystrix.class)
public interface MetadataFeignClient {
    String PREFIX_PATH = "/api/metadata";

    @PostMapping(value = PREFIX_PATH + "/workflow", consumes = MediaType.APPLICATION_JSON_VALUE)
    void registerWorkflowDef(@RequestBody WorkflowDef workflowDef);

    @DeleteMapping(PREFIX_PATH + "/workflow/{name}/{version}")
    void unregisterWorkflowDef(@PathVariable("name") String name, @PathVariable("version") Integer version);

}
