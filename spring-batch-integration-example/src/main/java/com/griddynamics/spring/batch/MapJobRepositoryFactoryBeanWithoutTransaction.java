package com.griddynamics.spring.batch;

import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.*;
import org.springframework.beans.factory.FactoryBean;


public class MapJobRepositoryFactoryBeanWithoutTransaction implements FactoryBean {


	protected JobExecutionDao createJobExecutionDao() throws Exception {
		return new MapJobExecutionDao();
	}

	protected JobInstanceDao createJobInstanceDao() throws Exception {
		return new MapJobInstanceDao();
	}


	protected StepExecutionDao createStepExecutionDao() throws Exception {
		return new MapStepExecutionDao();
	}


	protected ExecutionContextDao createExecutionContextDao() throws Exception {
		return new MapExecutionContextDao();
	}


    public Object getObject() throws Exception {
        return new SimpleJobRepository(createJobInstanceDao(), createJobExecutionDao(), createStepExecutionDao(),
				createExecutionContextDao());
    }


    public Class<JobRepository> getObjectType() {
            return JobRepository.class;
        }


    
    public boolean isSingleton() {
        return true;
    }
}
