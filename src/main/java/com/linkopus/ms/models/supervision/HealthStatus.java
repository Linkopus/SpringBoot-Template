package com.linkopus.ms.models.supervision;

public class HealthStatus {
	private String message;

	public HealthStatus(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}