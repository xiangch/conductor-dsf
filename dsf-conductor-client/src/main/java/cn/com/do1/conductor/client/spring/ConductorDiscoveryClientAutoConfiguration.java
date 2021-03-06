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

    @Bean
    public ConductorHttpMessageConverter conductorHttpMessageConverter() {

        ConductorHttpMessageConverter converter = new ConductorHttpMessageConverter();
        //??????fastJson???????????????
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
            // ??????????????????null?????????,?????????false
            SerializerFeature.WriteMapNullValue,
            // ???Collection????????????????????????????????????[]
            SerializerFeature.WriteNullListAsEmpty,
            // ???????????????????????????????????????0
            SerializerFeature.WriteNullNumberAsZero,
            //Boolean???????????????null,?????????false,??????null
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteDateUseDateFormat,
            //??????????????????????????????
            SerializerFeature.WriteEnumUsingToString,
            // ??????????????????
            SerializerFeature.DisableCircularReferenceDetect);
        //????????????????????????????????????yyyy-MM-dd HH:mm:ss
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);

        //3????????????????????????
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        //4.???convert?????????????????????.
        converter.setSupportedMediaTypes(fastMediaTypes);
        converter.setFastJsonConfig(fastJsonConfig);
        return converter;
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
