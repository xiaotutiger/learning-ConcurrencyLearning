package com.taobao.ju.akka.cal.filesize;

import java.io.File;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.taobao.ju.akka.message.obj.FileSize;
import com.taobao.ju.akka.message.obj.FileToProcess;
import com.taobao.ju.akka.message.obj.RequestAFile;

public class FileProcessor extends UntypedActor {
	private final ActorRef sizeCollector;

	public FileProcessor(final ActorRef theSizeCollector) {
		sizeCollector = theSizeCollector;
	}

	@Override
	public void preStart() {
		registerToGetFile();
	}

	public void registerToGetFile() {
		sizeCollector.tell(new RequestAFile(), getSelf());
	}

	public void onReceive(final Object message) {
		FileToProcess fileToProcess = (FileToProcess) message;
		final File file = new java.io.File(fileToProcess.getFileName());
		long size = 0L;
		if (file.isFile()) {
			size = file.length();
		} else {
			File[] children = file.listFiles();
			if (children != null)
				for (File child : children)
					if (child.isFile())
						size += child.length();
					else
						sizeCollector.tell(new FileToProcess(child.getPath()),
								getSelf());
		}

		sizeCollector.tell(new FileSize(size), getSelf());
		registerToGetFile();
	}
}