package com.taobao.ju.tips;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.Random;

public class MapThroughput {

	final private static int MAXLOOP = 1000;
	final private static int KEYLIMIT = 100;
	final private static ExecutorService service = Executors
			.newFixedThreadPool(100);
	final private static int NORM = 10000;
	final private static int MAXTHREADS = 16;

	private static void useMap(final Map<Integer, Integer> theMap,
			final int seed) {
		Random rand = new Random(seed * seed);
		for (int j = 0; j < MAXLOOP; j++) {
			int randomKey = rand.nextInt(KEYLIMIT);
			int randomValue = rand.nextInt(10);
			if (theMap.get(randomKey) == null) {
				if (randomValue < 8)
					theMap.put(randomKey, randomKey);
			} else {
				if (randomValue < 2)
					theMap.remove(randomKey);
			}
		}
	}

	private static long measureThroughput(final Map<Integer, Integer> theMap,
			int numberOfThreads) throws InterruptedException,
			ExecutionException {

		long start = System.nanoTime();

		List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
		for (int i = 0; i < numberOfThreads - 1; i++) {
			final int index = 0;
			callables.add(new Callable<Integer>() {
				public Integer call() {
					useMap(theMap, 100 + index);
					return 0;
				}
			});
		}

		List<Future<Integer>> futures = service.invokeAll(callables);
		useMap(theMap, 10);

		for (Future<Integer> future : futures)
			future.get();
		long end = System.nanoTime();

		return end - start;
	}

	public static int run(final Map<Integer, Integer> theMap,
			final int numberOfThreads) throws InterruptedException,
			ExecutionException {

		int timeTaken = 0;
		for (int i = 0; i < NORM; i++) {
			long b = measureThroughput(theMap, numberOfThreads);
			timeTaken += (long) (b * 1.0 / NORM);
		}

		return timeTaken;
	}

	public static void main(final String[] args) throws InterruptedException,
			ExecutionException {

		long baseTime = run(new ConcurrentHashMap<Integer, Integer>(), 1);

		for (int i = 1; i <= MAXTHREADS; i++) {
			long timeTaken = run(new ConcurrentHashMap<Integer, Integer>(), i);
			System.out.println(String.format(
					"Number of threads: %d Time: %2.2g", i, baseTime * i * 1.0
							/ timeTaken));
		}

		for (int i = 1; i <= MAXTHREADS; i++) {
			long timeTaken = run(
					Collections
							.synchronizedMap(new HashMap<Integer, Integer>()),
					i);
			System.out.println(String.format(
					"Number of threads: %d Time: %2.2g", i, baseTime * i * 1.0
							/ timeTaken));
		}

		service.shutdown();
	}
}
