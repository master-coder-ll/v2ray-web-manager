package com.jhl.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
@Configuration
public class AppConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         executor.setCorePoolSize(1);
          executor.setMaxPoolSize(2);
          executor.setQueueCapacity(5);
         executor.setThreadNamePrefix("task-AsyncExecutor-");
         executor.initialize();
         return executor;
    }
}
