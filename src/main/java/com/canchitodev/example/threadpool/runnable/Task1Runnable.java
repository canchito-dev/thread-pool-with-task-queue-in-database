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

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.canchitodev.example.domain.GenericTaskEntity;
import com.canchitodev.example.threadpool.runnable.TaskRunnable;

public class Task1Runnable implements TaskRunnable {
	
	private static final Logger logger = Logger.getLogger(Task1Runnable.class);
	
	private GenericTaskEntity task;

	public Task1Runnable() {}
	
	public Task1Runnable(GenericTaskEntity task) {
		this.task = task;
	}

	public GenericTaskEntity getTask() {
		return task;
	}

	public void setTask(GenericTaskEntity task) {
		this.task = task;
	}

	@Override
	public void execute() {
		try {
			logger.info("Executing task " + task.toString());
			TimeUnit.SECONDS.sleep(12);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			logger.info("Done executing task " + task.toString());
		}
	}
}