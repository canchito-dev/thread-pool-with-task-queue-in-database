package com.canchitodev.example;

import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.canchitodev.example.domain.GenericTaskEntity;
import com.canchitodev.example.service.GenericTaskService;
import com.canchitodev.example.utils.StrongUuidGenerator;

@SpringBootApplication
public class ThreadPoolWithTaskQueueInDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreadPoolWithTaskQueueInDatabaseApplication.class, args);
	}
	
	@Autowired
	private GenericTaskService genericTaskService;
	
	@Autowired
	private StrongUuidGenerator strongUuidGenerator;
	
//	@Bean
//	InitializingBean createTasksInitializer() {
//
//	    return new InitializingBean() {
//	        public void afterPropertiesSet() throws Exception {
//
//	        	GenericTaskEntity task = new GenericTaskEntity();
//	    		task.setDetails(new JSONObject());
//	    		task.setExecutionId(strongUuidGenerator.getNextId());
//	    		task.setPriority(new Random().nextInt(100 - 1) + 1);
//	    		task.setProcessDefinitionId(strongUuidGenerator.getNextId());
//	    		task.setProcessInstanceId(strongUuidGenerator.getNextId());
//	    		task.setTenantId("canchito-dev.com");
//	    		task.setBeanId("task1Runnable");
//	    		genericTaskService.save(task);
//	    		
//	    		task = new GenericTaskEntity();
//	    		task.setDetails(new JSONObject());
//	    		task.setExecutionId(strongUuidGenerator.getNextId());
//	    		task.setPriority(new Random().nextInt(100 - 1) + 1);
//	    		task.setProcessDefinitionId(strongUuidGenerator.getNextId());
//	    		task.setProcessInstanceId(strongUuidGenerator.getNextId());
//	    		task.setTenantId("canchito-dev.com");
//	    		task.setBeanId("task1Runnable");
//	    		genericTaskService.save(task);
//	    		
//	    		task = new GenericTaskEntity();
//	    		task.setDetails(new JSONObject());
//	    		task.setExecutionId(strongUuidGenerator.getNextId());
//	    		task.setPriority(new Random().nextInt(100 - 1) + 1);
//	    		task.setProcessDefinitionId(strongUuidGenerator.getNextId());
//	    		task.setProcessInstanceId(strongUuidGenerator.getNextId());
//	    		task.setTenantId("canchito-dev.com");
//	    		task.setBeanId("task1Runnable");
//	    		genericTaskService.save(task);
//	    		
//	    		task = new GenericTaskEntity();
//	    		task.setDetails(new JSONObject());
//	    		task.setExecutionId(strongUuidGenerator.getNextId());
//	    		task.setPriority(new Random().nextInt(100 - 1) + 1);
//	    		task.setProcessDefinitionId(strongUuidGenerator.getNextId());
//	    		task.setProcessInstanceId(strongUuidGenerator.getNextId());
//	    		task.setTenantId("canchito-dev.com");
//	    		task.setBeanId("task1Runnable");
//	    		genericTaskService.save(task);
//	    		
//	    		task = new GenericTaskEntity();
//	    		task.setDetails(new JSONObject());
//	    		task.setExecutionId(strongUuidGenerator.getNextId());
//	    		task.setPriority(new Random().nextInt(100 - 1) + 1);
//	    		task.setProcessDefinitionId(strongUuidGenerator.getNextId());
//	    		task.setProcessInstanceId(strongUuidGenerator.getNextId());
//	    		task.setTenantId("canchito-dev.com");
//	    		task.setBeanId("task1Runnable");
//	    		genericTaskService.save(task);
//	        }
//	    };
//	}
}
