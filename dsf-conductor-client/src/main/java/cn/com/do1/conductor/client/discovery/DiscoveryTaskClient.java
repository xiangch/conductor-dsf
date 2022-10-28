package cn.com.do1.conductor.client.discovery;

import cn.com.do1.conductor.client.discovery.feign.TaskFeignClient;
import com.google.common.base.Preconditions;
import com.netflix.conductor.client.config.ConductorClientConfiguration;
import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.telemetry.MetricsContainer;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.run.ExternalStorageLocation;
import com.netflix.conductor.common.utils.ExternalPayloadStorage;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.Optional;

/**
 * @author zengxc
 */

public class DiscoveryTaskClient extends TaskClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryTaskClient.class);
    private TaskFeignClient feign;
    private  final  PayloadStorage payloadStorage;

    public DiscoveryTaskClient(TaskFeignClient feign, ConductorClientConfiguration conductorClientConfiguration) {
        super(new DefaultClientConfig(),conductorClientConfiguration,null);
        this.feign = feign;
        payloadStorage = new PayloadStorage(feign);
    }

    @Override
    public Task pollTask(String taskType, String workerId, String domain) {
        Preconditions.checkArgument(!StringUtils.isEmpty(taskType), "Task type cannot be blank");
        Preconditions.checkArgument(!StringUtils.isEmpty(workerId), "Worker id cannot be blank");
        Task task = Optional.ofNullable(
            feign.pollTask(taskType, workerId, domain)
        ).orElse(new Task());
        return task;
    }

    @Override
    public void updateTask(TaskResult taskResult) {
        Preconditions.checkNotNull(taskResult, "Task result cannot be null");
        feign.updateTask(taskResult);
    }

    @Override
    public Optional<String> evaluateAndUploadLargePayload(
        Map<String, Object> taskOutputData, String taskType) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            objectMapper.writeValue(byteArrayOutputStream, taskOutputData);
            byte[] taskOutputBytes = byteArrayOutputStream.toByteArray();
            long taskResultSize = taskOutputBytes.length;
            MetricsContainer.recordTaskResultPayloadSize(taskType, taskResultSize);

            long payloadSizeThreshold =
                conductorClientConfiguration.getTaskOutputPayloadThresholdKB() * 1024L;
            if (taskResultSize > payloadSizeThreshold) {
                if (!conductorClientConfiguration.isExternalPayloadStorageEnabled()
                    || taskResultSize
                    > conductorClientConfiguration.getTaskOutputMaxPayloadThresholdKB()
                    * 1024L) {
                    throw new IllegalArgumentException(
                        String.format(
                            "The TaskResult payload size: %d is greater than the permissible %d bytes",
                            taskResultSize, payloadSizeThreshold));
                }
                MetricsContainer.incrementExternalPayloadUsedCount(
                    taskType,
                    ExternalPayloadStorage.Operation.WRITE.name(),
                    ExternalPayloadStorage.PayloadType.TASK_OUTPUT.name());
                return Optional.of(
                    uploadToExternalPayloadStorage(
                        ExternalPayloadStorage.PayloadType.TASK_OUTPUT, taskOutputBytes, taskResultSize));
            }
            return Optional.empty();
        } catch (IOException e) {
            String errorMsg = String.format("Unable to update task: %s with task result", taskType);
            LOGGER.error(errorMsg, e);
            throw new ConductorClientException(errorMsg, e);
        }
    }

    @Override
    public String uploadToExternalPayloadStorage(
        ExternalPayloadStorage.PayloadType payloadType, byte[] payloadBytes, long payloadSize) {
        Preconditions.checkArgument(
            payloadType.equals(ExternalPayloadStorage.PayloadType.WORKFLOW_INPUT)
                || payloadType.equals(ExternalPayloadStorage.PayloadType.TASK_OUTPUT),
            "Payload type must be workflow input or task output");
        ExternalStorageLocation externalStorageLocation =
            payloadStorage.getLocation(ExternalPayloadStorage.Operation.WRITE, payloadType, "");
        payloadStorage.upload(
            externalStorageLocation.getUri(),
            new ByteArrayInputStream(payloadBytes),
            payloadSize);
        return externalStorageLocation.getPath();
    }


}
