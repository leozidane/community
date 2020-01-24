package com.zk.community.config;

import com.zk.community.quartz.AlphaQuartz;
import com.zk.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    //FactoryBean可以简化Bean的实例化过程
    //1.通过FactoryBean封装Bean的实例化过程
    //2.将FactoryBean对象加入到Spring容器中
    //3.将FactoryBean注入给其他的类
    //4.然后注入的是该FactoryBean管理的对象实例
    //配置JobDetail
    //@Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaQuartz.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        //声明该任务是否长久储存
        factoryBean.setDurability(true);
        //声明任务是否可以恢复(当应用程序出现问题时)
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //配置Trigger触发器(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    //SimpleTriggerFactoryBean可以进行简单的配置
    //CronTriggerFactoryBean可以进行复杂的配置，比如将来某个确定的时刻触发任务
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setGroup("alphaTrigger");
        factoryBean.setName("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        //Trigger记录Job的状态：比如上一次任务执行的期间以及下一次任务执行的时间
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    //刷新分数任务JobDetail
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        //声明该任务是否长久储存
        factoryBean.setDurability(true);
        //声明任务是否可以恢复(当应用程序出现问题时)
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //刷新分数任务的触发器Trigger
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setGroup("postScoreRefreshTrigger");
        factoryBean.setName("communityTriggerGroup");
        //五分钟执行一次
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        //Trigger记录Job的状态：比如上一次任务执行的期间以及下一次任务执行的时间
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
