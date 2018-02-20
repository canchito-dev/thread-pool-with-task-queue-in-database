/**
 * This content is released under the MIT License (MIT)
 *
 * Copyright (c) 2018, canchito-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author 		José Carlos Mendoza Prego
 * @copyright	Copyright (c) 2018, canchito-dev (http://www.canchito-dev.com)
 * @license		http://opensource.org/licenses/MIT	MIT License
 * @link		https://github.com/canchito-dev/thread-pool-with-task-queue-in-database
 **/
package com.canchitodev.example.service;

import java.util.List;

import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canchitodev.example.domain.GenericTaskEntity;
import com.canchitodev.example.exception.ObjectNotFoundException;
import com.canchitodev.example.repository.GenericTaskRepository;
import com.canchitodev.example.utils.StrongUuidGenerator;
import com.canchitodev.example.utils.enums.BehaviorTaskStatus;

@Service
@DependsOn("applicationContextProvider")
@Scope("prototype")
public class GenericTaskService {
	
	private static final Logger logger = Logger.getLogger(GenericTaskService.class);
	
	private Object lock = new Object();
	
	@Autowired
	private GenericTaskRepository genericTaskRepository;
	
	@Autowired
	private StrongUuidGenerator strongUuidGenerator;
	
	public GenericTaskService() {}
	
	public void save(GenericTaskEntity task) {
		synchronized (lock) {
			if(task.getUuid() == null)
				task.setUuid(this.strongUuidGenerator.getNextId());
			
			if(task.getStatus() == null)
				task.setStatus(BehaviorTaskStatus.WAITING.getStatus());
			
			this.genericTaskRepository.save(task);
			logger.info("Generic task '" + task.toString() + "' saved");
		}
	}
	
	public void update(GenericTaskEntity task) {
		synchronized (lock) {
			GenericTaskEntity entity = this.findByUuid(task.getUuid());
			
			if(task.getBeanId() == null)
				task.setBeanId(entity.getBeanId());
			
			if(task.getDetails() == null)
				task.setDetails(entity.getDetails());
			
			if(task.getExecutionId() == null)
				task.setExecutionId(entity.getExecutionId());
			
			if(task.getLockOwner() == null)
				task.setLockOwner(entity.getLockOwner());
			
			if(task.getPriority() == null)
				task.setPriority(entity.getPriority());
			
			if(task.getProcessDefinitionId() == null)
				task.setProcessDefinitionId(entity.getProcessDefinitionId());
			
			if(task.getProcessInstanceId() == null)
				task.setProcessInstanceId(entity.getProcessInstanceId());
			
			if(task.getStatus() == null)
				task.setStatus(entity.getStatus());
			
			if(task.getTenantId() == null)
				task.setTenantId(entity.getTenantId());
			
			if(task.getUuid() == null)
				task.setUuid(entity.getUuid());
			
			this.genericTaskRepository.save(task);
			logger.info("Generic task '" + task.toString() + "' updated");
		}
	}
	
	public void delete(GenericTaskEntity task) {
		synchronized (lock) {
			this.genericTaskRepository.delete(task);
			logger.info("Generic task '" + task.toString() + "' deleted");
		}
	}
	
	public GenericTaskEntity findByProcessDefinitionIdAndProcessInstanceIdAndExecutionId(String processDefinitionId, String processInstanceId, String executionId) {
		GenericTaskEntity entity = this.genericTaskRepository.findByProcessDefinitionIdAndProcessInstanceIdAndExecutionId(processDefinitionId, processInstanceId, executionId);
		
		if(entity == null)
			throw new ObjectNotFoundException("Could not find generic entity task  with process definition id '" + processDefinitionId 
					+ "', process instance id '" + processInstanceId + "' and execution id '" + executionId + "'");
		
		return entity;
	}
	
	public GenericTaskEntity findByUuid(String uuid) {
		GenericTaskEntity entity = this.genericTaskRepository.findByUuid(uuid);
		
		if(entity == null)
			throw new ObjectNotFoundException("Could not find generic entity task  with uuid '" + uuid + "'");
		
		return entity;
	}
	
	public List<GenericTaskEntity> findAll() {
		return this.genericTaskRepository.findAll();
	}
	
	public GenericTaskEntity findByExecutionId(String executionId) {
		GenericTaskEntity entity = this.genericTaskRepository.findByExecutionId(executionId);
		
		if(entity == null)
			throw new ObjectNotFoundException("Could not find generic entity task  with execution id '" + executionId + "'");
		
		return entity;
	}
	
	public Page<GenericTaskEntity> findByBeanIdAndTenantIdAndLockOwnerIsNullOrderByPriorityDesc(String beanId, String tenantId, Pageable pageable) {
		return this.genericTaskRepository.findByBeanIdAndTenantIdAndLockOwnerIsNullOrderByPriorityDesc(beanId, tenantId, pageable);
	}
	
	public GenericTaskEntity findByUuidAndLockOwner(String uuid, String lockOwner) {
		GenericTaskEntity entity = this.genericTaskRepository.findByUuidAndLockOwner(uuid, lockOwner);
		
		if(entity == null)
			throw new ObjectNotFoundException("Could not find generic entity task  with uuid '" + uuid 
					+ "' and lock owner '" + lockOwner + "'");
		
		return entity;
	}
	
	@Transactional
	public int setTaskStatus(Integer status, String uuid, String lockOwner) {
		this.findByUuidAndLockOwner(uuid, lockOwner);
		return this.genericTaskRepository.setTaskStatus(status, uuid, lockOwner);
	}
}