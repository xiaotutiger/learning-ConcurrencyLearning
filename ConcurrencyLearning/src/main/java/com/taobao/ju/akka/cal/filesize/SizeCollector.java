package com.taobao.ju.akka.cal.filesize;

import java.util.ArrayList;
import java.util.List;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.taobao.ju.akka.message.obj.FileSize;
import com.taobao.ju.akka.message.obj.FileToProcess;
import com.taobao.ju.akka.message.obj.RequestAFile;

public class SizeCollector extends UntypedActor {
	private final List<String> toProcessFileNames = new ArrayList<String>();
	private final List<ActorRef> idleFileProcessors = new ArrayList<ActorRef>();
	private long pendingNumberOfFilesToVisit = 0L;
	private long totalSize = 0L;
	private long start = System.nanoTime();

	
//	private ActorRef fileProcessor = getContext().actorOf(
//			new Props(new UntypedActorFactory() {
//				public UntypedActor create() {
//					return new FileProcessor(getSelf());
//				}
//			}), "fileProcessor");
	
	public void sendAFileToProcess() {
		if (!toProcessFileNames.isEmpty() && !idleFileProcessors.isEmpty())
			idleFileProcessors.remove(0).tell(
					new FileToProcess(toProcessFileNames.remove(0)), getSelf());
	}

	public void onReceive(final Object message) {
		if (message instanceof RequestAFile) {
			idleFileProcessors.add(getSender());
			sendAFileToProcess();
		}

		if (message instanceof FileToProcess) {
			toProcessFileNames.add(((FileToProcess) (message)).getFileName());
			pendingNumberOfFilesToVisit += 1;
			sendAFileToProcess();
		}

		if (message instanceof FileSize) {
			totalSize += ((FileSize) (message)).getSize();
			pendingNumberOfFilesToVisit -= 1;

			if (pendingNumberOfFilesToVisit == 0) {
				long end = System.nanoTime();
				System.out.println("Total size is " + totalSize);
				System.out.println("Time taken is " + (end - start) / 1.0e9);
				getContext().system().shutdown();
			}
		}
	}
}