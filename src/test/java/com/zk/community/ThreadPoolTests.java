package com.zk.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可定时执行任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    //Spring定时线程池
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(long m) {
        try {
            Thread.currentThread().sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testExecutorService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };
        for (int i = 1; i < 10; i++) {
            executorService.submit(runnable);
        }
        sleep(10000);
    }

    @Test
    public void testScheduledExecutorService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };
        //设置延时时间以及执行任务的周期
        scheduledExecutorService.scheduleAtFixedRate(runnable, 10, 1, TimeUnit.SECONDS);
        sleep(30000);
    }

    @Test
    public void testTaskExecutor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello taskExecutor");
            }
        };
        for (int i = 1; i < 10; i++) {
            taskExecutor.submit(runnable);
        }
        sleep(10000);
    }
    @Test
    public void testTaskScheduler() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello taskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(runnable, startTime, 1000);
        sleep(30000);
    }
}
