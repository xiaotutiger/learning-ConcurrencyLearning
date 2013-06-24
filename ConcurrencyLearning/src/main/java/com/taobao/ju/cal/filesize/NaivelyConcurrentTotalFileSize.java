package com.taobao.ju.cal.filesize;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author feize
 *
 */
public class NaivelyConcurrentTotalFileSize {
	private long getTotalSizeOfFilesInDir(final ExecutorService service,
			final File file) throws InterruptedException, ExecutionException,
			TimeoutException {
		if (file.isFile())
			return file.length();

		long total = 0;
		final File[] children = file.listFiles();

		if (children != null) {
			final List<Future<Long>> partialTotalFutures = new ArrayList<Future<Long>>();
			for (final File child : children) {
				partialTotalFutures.add(service.submit(new Callable<Long>() {
					public Long call() throws InterruptedException,
							ExecutionException, TimeoutException {
						return getTotalSizeOfFilesInDir(service, child);
					}
				}));
			}

			for (final Future<Long> partialTotalFuture : partialTotalFutures)
				total += partialTotalFuture.get(1, TimeUnit.SECONDS);
		}

		return total;
	}

	private long getTotalSizeOfFile(final String fileName)
			throws InterruptedException, ExecutionException, TimeoutException {
		final ExecutorService service = Executors.newFixedThreadPool(10);
		try {
			return getTotalSizeOfFilesInDir(service, new File(fileName));
		} finally {
			service.shutdown();
		}
	}

	public static void main(final String[] args) throws InterruptedException,
			ExecutionException, TimeoutException {
		final long start = System.nanoTime();
		final long total = new NaivelyConcurrentTotalFileSize()
				.getTotalSizeOfFile("/Users/TWL/Dev/openSource");
		final long end = System.nanoTime();
		System.out.println("Total Size: " + total);
		System.out.println("Time taken: " + (end - start) / 1.0e9);
	}
}
