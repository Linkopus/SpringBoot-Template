package com.linkopus.ms.middlewares.errorHandlers.ApiError;

import com.linkopus.ms.utils.ressources.enums.ErrorTypes;
import org.springframework.http.HttpStatus;

public class ErrorBody {
	private HttpStatus status;
	private ErrorTypes name;
	private String details;

	public ErrorBody(HttpStatus status, ErrorTypes name, String details) {
		this.status = status;
		this.name = name;
		this.details = details;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public ErrorTypes getName() {
		return name;
	}

	public void setName(ErrorTypes name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "ErrorBody{" + "status=" + status.toString() + ", name='" + name + '\'' + ", details='" + details + '\''
				+ '}';
	}
}
