package cn.com.do1.conductor.client.spring;

import cn.com.do1.conductor.client.discovery.DiscoveryTaskClient;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public TaskClient taskClient(ClientProperties clientProperties) {
        return new DiscoveryTaskClient(restTemplate,clientProperties.getRootUri());
    }


    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public TaskRunnerConfigurer taskRunnerConfigurer(
            TaskClient taskClient, ClientProperties clientProperties) {
        return   new TaskRunnerConfigurer.Builder(taskClient, workers)
                .withTaskThreadCount(clientProperties.getTaskThreadCount())
                .withThreadCount(clientProperties.getThreadCount())
                .withSleepWhenRetry((int) clientProperties.getSleepWhenRetryDuration().toMillis())
                .withUpdateRetryCount(clientProperties.getUpdateRetryCount())
                .withTaskToDomain(clientProperties.getTaskToDomain())
                .withShutdownGracePeriodSeconds(clientProperties.getShutdownGracePeriodSeconds())
                .build();

    }
}
