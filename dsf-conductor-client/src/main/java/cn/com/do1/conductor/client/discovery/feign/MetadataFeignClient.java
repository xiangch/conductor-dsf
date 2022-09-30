package cn.com.do1.conductor.client.discovery.feign;

import cn.com.do1.conductor.client.discovery.feign.hystrix.MetadataFeignClientHystrix;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import javax.ws.rs.PathParam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zengxc
 */
@FeignClient(name = "conductor", contextId = "Conductor.MetadataFeignClient", url = "${conductor.client.rootUri:}", fallback = MetadataFeignClientHystrix.class)
public interface MetadataFeignClient {
    String PREFIX_PATH = "/api/metadata";

    @GetMapping(value = PREFIX_PATH + "/workflow/{name}")
    WorkflowDef getWorkflowDef(@PathVariable("name") String name,@RequestParam("version")Integer version);

    @PostMapping(value = PREFIX_PATH + "/workflow", consumes = MediaType.APPLICATION_JSON_VALUE)
    void registerWorkflowDef(@RequestBody WorkflowDef workflowDef);

    @DeleteMapping(PREFIX_PATH + "/workflow/{name}/{version}")
    void unregisterWorkflowDef(@PathVariable("name") String name, @PathVariable("version") Integer version);
    

   
}
