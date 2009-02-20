package com.griddynamics.spring.batch;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JobInstanceAlreadyCompleteException, JobRestartException, JobExecutionAlreadyRunningException {
        GenericApplicationContext context = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
        xmlReader.loadBeanDefinitions(new ClassPathResource("test-work-context.xml"));
        context.refresh();

        Job job = (Job) context.getBean("testJob");
        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");

        
        HashMap<String, JobParameter> map = new HashMap<String, JobParameter>();
        map.put("test",new JobParameter(10L));

        jobLauncher.run(job,  new JobParameters(map));
    }
}
