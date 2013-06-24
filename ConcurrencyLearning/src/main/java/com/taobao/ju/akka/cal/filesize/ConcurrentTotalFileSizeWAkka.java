package com.taobao.ju.akka.cal.filesize;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.actor.ActorRef;
import com.taobao.ju.akka.message.obj.FileToProcess;


public class ConcurrentTotalFileSizeWAkka {
	public static void main(final String[] args) {
		ActorSystem actorSystem = ActorSystem.create("ConcurrentFileSizeWAkka");
//		ActorSystem actorSystem = ActorSystem.create("ConcurrentFileSizeWAkka",ConfigFactory.load().getConfig("MyRouterExample"));
		final ActorRef sizeCollector = actorSystem.actorOf(new Props(
				SizeCollector.class), "SizeCollector");

		for (int i = 0; i < 100; i++) {
			actorSystem.actorOf(new Props(new UntypedActorFactory() {
				public UntypedActor create() {
					return new FileProcessor(sizeCollector);
				}
			}), "FileProcessor" + i);

		}

		sizeCollector.tell(new FileToProcess("/Users/TWL/Dev/openSource"), sizeCollector);

	}

}
