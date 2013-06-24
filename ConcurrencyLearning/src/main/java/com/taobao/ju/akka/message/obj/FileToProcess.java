package com.taobao.ju.akka.message.obj;

public class FileToProcess {
	private final String fileName;

	public FileToProcess(final String name) {
		fileName = name;
	}

	public String getFileName() {
		return fileName;
	}
}
