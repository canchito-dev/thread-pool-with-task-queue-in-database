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
package com.canchitodev.example.threadpool.runnable;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TaskRunnableFactory {
	
	private ApplicationContext applicationContext;
	
	public TaskRunnableFactory() {
		applicationContext = new ClassPathXmlApplicationContext("task-runnable-beans.xml");
	}

	public TaskRunnable getRunnable(String beanId) {
		if(beanId == null || beanId.isEmpty())
			return null;
		
		if(!this.applicationContext.containsBean(beanId))
			return null;
		
		return (TaskRunnable) this.applicationContext.getBean(beanId);
	}
	
	public Map<String, TaskRunnable> getAllRunnables() {
		Map<String, TaskRunnable> queues = new HashMap<String, TaskRunnable>();
		for(String beanName: applicationContext.getBeanDefinitionNames()) {
			queues.put(beanName, applicationContext.getBean(beanName, TaskRunnable.class));
		}
		return queues;
	}
}