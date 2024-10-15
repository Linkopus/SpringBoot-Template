package com.linkopus.ms.RabbitMqClient.publisher;

import java.io.Serializable;

public class Request implements Serializable {
	private static final long serialVersionUID = 1L;

	private String publisherApikey;
	private Object data;

	public String getPublisherApikey() {
		return publisherApikey;
	}

	public void setPublisherApikey(String publisherApikey) {
		this.publisherApikey = publisherApikey;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
