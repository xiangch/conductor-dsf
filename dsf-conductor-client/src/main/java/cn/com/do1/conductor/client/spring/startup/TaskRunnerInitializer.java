package cn.com.do1.conductor.client.spring.startup;

import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskRunnerInitializer {
    private final TaskRunnerConfigurer taskRunnerConfigurer;

    public TaskRunnerInitializer(TaskRunnerConfigurer taskRunnerConfigurer){
        this.taskRunnerConfigurer =taskRunnerConfigurer;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void setupTaskRunner(){
        taskRunnerConfigurer.init();
    }
}
