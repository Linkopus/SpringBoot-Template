package com.linkopus.ms.middlewares.errorHandlers.ApiError;

import org.slf4j.Logger;

public class ApiError extends Exception {

	private final ErrorBody errorBody;

	public ApiError(ErrorBody errorBody, Logger logger) {
		super(errorBody.getStatus() + " " + errorBody.getName());
		this.errorBody = errorBody;

		logger.error(this.log());
	}

	public String log() {
		return errorBody.getStatus().value() + " " + errorBody.getName() + ": "
				+ (errorBody.getDetails() != null ? errorBody.getDetails() : "");
	}

	public ErrorBody getErrorBody() {
		return errorBody;
	}
}
