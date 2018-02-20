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
package com.canchitodev.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.canchitodev.example.domain.GenericTaskEntity;

@Repository
public interface GenericTaskRepository extends JpaRepository<GenericTaskEntity, String>, JpaSpecificationExecutor<GenericTaskEntity> {
	GenericTaskEntity findByProcessDefinitionIdAndProcessInstanceIdAndExecutionId(String processDefinitionId, String processInstanceId, String executionId);
	GenericTaskEntity findByUuid(String uuid);
	GenericTaskEntity findByUuidAndLockOwner(String uuid, String lockOwner);
	GenericTaskEntity findByExecutionId(String executionId);
	
	Page<GenericTaskEntity> findByBeanIdAndTenantIdAndLockOwnerIsNullOrderByPriorityDesc(String beanId, String tenantId, Pageable pageable);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE GenericTaskEntity task SET task.status = ?1 WHERE task.uuid = ?2 AND task.lockOwner = ?3")
	int setTaskStatus(Integer status, String uuid, String lockOwner);
}
