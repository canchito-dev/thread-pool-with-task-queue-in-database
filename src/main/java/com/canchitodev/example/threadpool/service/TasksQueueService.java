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
package com.canchitodev.example.threadpool.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.canchitodev.example.threadpool.utils.AcquireTaskThread;
import com.canchitodev.example.utils.StrongUuidGenerator;

/**
 * The TaskQueueService is in charge of managing the different threads that acquire the pending to execute tasks
 **/
@Service
public class TasksQueueService {
	
	private static final Logger logger = Logger.getLogger(TasksQueueService.class);
	
	private HashMap<String, AcquireTaskThread> acquireTaskThread;
	
	@Value("${server.tenant-Id}")
	private String serverTenantId = "canchito-dev.com";
	
	@Autowired
	private TaskQueueFactory taskQueueFactory;
	
	@Autowired
	private StrongUuidGenerator strongUuidGenerator;

	public TasksQueueService() {
		this.acquireTaskThread = new HashMap<String, AcquireTaskThread>();
	}

	@PostConstruct
	private void onInit() {
		Map<String, TaskQueue> queues = this.taskQueueFactory.getAllTaskQueues();
		
		Set<String> keys = queues.keySet();
		
		for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
			String beanName = (String) i.next();
			
			TaskQueue taskQueue = (TaskQueue) queues.get(beanName);
			String asyncThreadName = "acquire-" + taskQueue.getPoolName();

			logger.info("Initializing acquire task thread '" + asyncThreadName + "'");
			this.acquireTaskThread.put(
					asyncThreadName, 
					new AcquireTaskThread(
							this.serverTenantId,
							this.strongUuidGenerator.getNextId(), 
							taskQueue
					)
			);
			
			logger.info("Starting async task thread '" + asyncThreadName + "'");
			this.acquireTaskThread.get(asyncThreadName).start();
		}    
	}
	
	/**
	 * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted. 
	 * Invocation has no additional effect if already shut down. This method does not wait for previously submitted tasks 
	 * to complete execution.
	 **/
	@PreDestroy
	public void onShutDown() {
		Set<String> keys = acquireTaskThread.keySet();
		
		for (Iterator<String> key = keys.iterator(); key.hasNext(); ) {
			String taskName = (String) key.next();
			logger.info("Shutting down acquire task thread '" + taskName + "'");
			this.acquireTaskThread.get(taskName).setShutDown(true);
		}		
	}
}