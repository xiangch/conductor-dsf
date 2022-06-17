package cn.com.do1.conductor.client.spring;

import cn.com.do1.conductor.client.discovery.DiscoveryMetadataClient;
import cn.com.do1.conductor.client.discovery.DiscoveryTaskClient;
import cn.com.do1.conductor.client.discovery.DiscoveryWorkflowClient;
import cn.com.do1.conductor.client.discovery.feign.MetadataFeignClient;
import cn.com.do1.conductor.client.discovery.feign.TaskFeignClient;
import cn.com.do1.conductor.client.discovery.feign.WorkflowFeignClient;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengxc
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ClientProperties.class)
public class ConductorDiscoveryClientAutoConfiguration {


    @Autowired(required = false)
    private List<Worker> workers = new ArrayList<>();

    @Bean
    public TaskClient taskClient(TaskFeignClient feignClient) {
        return new DiscoveryTaskClient(feignClient);
    }

    @Bean
    public DiscoveryWorkflowClient workflowClient(WorkflowFeignClient feignClient) {
        return new DiscoveryWorkflowClient(feignClient);
    }

    @Bean
    public DiscoveryMetadataClient metadataClient(MetadataFeignClient feignClient) {
        return new DiscoveryMetadataClient(feignClient);
    }


    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public TaskRunnerConfigurer taskRunnerConfigurer(
        TaskClient taskClient, ClientProperties clientProperties) {
        return new TaskRunnerConfigurer.Builder(taskClient, workers)
            .withTaskThreadCount(clientProperties.getTaskThreadCount())
            .withThreadCount(clientProperties.getThreadCount())
            .withSleepWhenRetry((int) clientProperties.getSleepWhenRetryDuration().toMillis())
            .withUpdateRetryCount(clientProperties.getUpdateRetryCount())
            .withTaskToDomain(clientProperties.getTaskToDomain())
            .withShutdownGracePeriodSeconds(clientProperties.getShutdownGracePeriodSeconds())
            .build();
    }
}
