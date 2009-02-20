package com.griddynamics.terracotta;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.StringUtils;
import commonj.work.Work;


public class BatchWork implements Work {
    final private String jobName;
    final private JobParameters jobParameters;
    private transient ConfigurableApplicationContext context;
    private transient JobLauncher launcher;
    private transient JobLocator jobLocator;

    public BatchWork(String jobName, JobParameters jobParameters) {
        this.jobName = jobName;
        this.jobParameters = jobParameters;
    }

    public synchronized String getJobName() {
        return jobName;
    }

    public synchronized JobParameters getJobParameters() {
        return jobParameters;
    }


    public synchronized ConfigurableApplicationContext getContext() {
        return context;
    }

    public synchronized void setContext(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public synchronized JobLauncher getLauncher() {
        return launcher;
    }

    public  synchronized void setLauncher(JobLauncher launcher) {
        this.launcher = launcher;
    }

    public  synchronized JobLocator getJobLocator() {
        return jobLocator;
    }

    public  synchronized void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public boolean isDaemon() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void release() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void run() {
        context.getAutowireCapableBeanFactory().autowireBeanProperties(this,
                AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);

        try {
            Job job;
            if (jobLocator != null) {
                job = jobLocator.getJob(getJobName());
            } else {
                job = (Job) context.getBean(getJobName());
            }

            JobExecution jobExecution = launcher.run(job, getJobParameters());

        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (NoSuchJobException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
