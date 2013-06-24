package com.taobao.ju.akka.message.obj;

public class FileSize {
	private final long size;

	public long getSize() {
		return size;
	}

	public FileSize(final long fileSize) {
		size = fileSize;
	}
}
