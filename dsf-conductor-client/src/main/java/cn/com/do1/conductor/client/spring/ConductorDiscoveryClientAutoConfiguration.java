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
@EnableFeignClients(clients = {TaskFeignClient.class,WorkflowFeignClient.class,MetadataFeignClient.class})
public class ConductorDiscoveryClientAutoConfiguration {


    @Autowired(required = false)
    private List<Worker> workers = new ArrayList<>();


    @ConditionalOnMissingBean
    @Bean
    FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        //2:添加fastJson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
            // 是否输出值为null的字段,默认为false
            SerializerFeature.WriteMapNullValue,
            // 将Collection类型字段的字段空值输出为[]
            SerializerFeature.WriteNullListAsEmpty,
            // 将字符串类型字段的空值输出为空字符串
            SerializerFeature.WriteNullStringAsEmpty,
            // 将数值类型字段的空值输出为0
            SerializerFeature.WriteNullNumberAsZero,
            //Boolean字段如果为null,输出为false,而非null
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteDateUseDateFormat,
            //枚举字段输出为枚举值
            SerializerFeature.WriteEnumUsingToString,
            // 禁用循环引用
            SerializerFeature.DisableCircularReferenceDetect);
        //这个日期格式是全局格式：yyyy-MM-dd HH:mm:ss
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);

        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        //4.在convert中添加配置信息.
        converter.setSupportedMediaTypes(fastMediaTypes);
        converter.setFastJsonConfig(fastJsonConfig);
        return converter;
    }

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
