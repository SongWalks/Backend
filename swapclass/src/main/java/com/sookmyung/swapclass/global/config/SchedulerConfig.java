package com.sookmyung.swapclass.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Scheduled 크론잡 전용 스케줄러.
 *
 * 이 설정이 없으면 @Scheduled 잡들이 웹소켓(STOMP) 메시지브로커의 TaskScheduler(스레드 2개)를
 * 공유해서 돌게 되고, 하트비트·릴레이 트래픽과 스레드를 경쟁하다 크론잡(만료 처리 등)이
 * 밀리거나 굶는 문제가 발생한다. 전용 스레드풀을 명시해 웹소켓과 완전히 분리한다.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);                       // 크론잡 6개가 서로 굶지 않도록
        scheduler.setThreadNamePrefix("swap-sched-");   // 로그에서 웹소켓과 구분
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(20);
        return scheduler;
    }
}
