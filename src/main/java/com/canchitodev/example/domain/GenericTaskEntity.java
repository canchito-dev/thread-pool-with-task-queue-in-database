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
package com.canchitodev.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.json.JSONObject;

import com.canchitodev.example.utils.fieldconverters.BlobJsonObjectConverter;

@Entity
@Table(name = "cwm_tasks_queue")
public class GenericTaskEntity implements Serializable {

	private static final long serialVersionUID = -4984499270335602207L;

	@Id	
	@Column(name = "UUID_", nullable = false, length = 255)
	private String uuid;
	
	@Column(name = "PROCESS_DEFINITION_ID_", nullable = false, length = 64)
	private String processDefinitionId;
	
	@Column(name = "PROCESS_INSTANCE_ID_", nullable = false, length = 64)
	private String processInstanceId;
	
	@Column(name = "EXECUTION_ID_", nullable = false, length = 64)
	private String executionId;
	
	@Min(0)
	@Max(100)
	@Column(name = "PRIORITY_", nullable = false)
	private Integer priority;
	
	@Convert(converter = BlobJsonObjectConverter.class)
	@Column(name = "DETAILS_", nullable = false)
	private JSONObject details;
	
	@Column(name = "BEAN_ID_", nullable = false, length = 64)
	private String beanId;
	
	@Column(name = "STATUS_", nullable = false)
	private Integer status;
	
	@Column(name = "TENANT_ID_", nullable = true)
	private String tenantId;
	
	@Version
	@Column(name = "VERSION_", nullable = true)
	private Integer version;
	
	@Column(name = "LOCK_OWNER_", nullable = true)
	private String lockOwner;
	
	public GenericTaskEntity() {}

	public GenericTaskEntity(String uuid, String processDefinitionId, String processInstanceId, String executionId,
			Integer priority, JSONObject details, String beanId, Integer status, String tenantId, Integer version,
			String lockOwner) {
		this.uuid = uuid;
		this.processDefinitionId = processDefinitionId;
		this.processInstanceId = processInstanceId;
		this.executionId = executionId;
		this.priority = priority;
		this.details = details;
		this.beanId = beanId;
		this.status = status;
		this.tenantId = tenantId;
		this.version = version;
		this.lockOwner = lockOwner;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public JSONObject getDetails() {
		return details;
	}

	public void setDetails(JSONObject details) {
		this.details = details;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getLockOwner() {
		return lockOwner;
	}

	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beanId == null) ? 0 : beanId.hashCode());
		result = prime * result + ((executionId == null) ? 0 : executionId.hashCode());
		result = prime * result + ((lockOwner == null) ? 0 : lockOwner.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((processDefinitionId == null) ? 0 : processDefinitionId.hashCode());
		result = prime * result + ((processInstanceId == null) ? 0 : processInstanceId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericTaskEntity other = (GenericTaskEntity) obj;
		if (beanId == null) {
			if (other.beanId != null)
				return false;
		} else if (!beanId.equals(other.beanId))
			return false;
		if (executionId == null) {
			if (other.executionId != null)
				return false;
		} else if (!executionId.equals(other.executionId))
			return false;
		if (lockOwner == null) {
			if (other.lockOwner != null)
				return false;
		} else if (!lockOwner.equals(other.lockOwner))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (processDefinitionId == null) {
			if (other.processDefinitionId != null)
				return false;
		} else if (!processDefinitionId.equals(other.processDefinitionId))
			return false;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GenericTaskEntity [uuid=" + uuid + ", processDefinitionId=" + processDefinitionId
				+ ", processInstanceId=" + processInstanceId + ", executionId=" + executionId + ", priority=" + priority
				+ ", details=" + details + ", beanId=" + beanId + ", status=" + status + ", tenantId=" + tenantId
				+ ", version=" + version + ", lockOwner=" + lockOwner + "]";
	}
}