/***
 * Excerpted from "Programming Concurrency on the JVM",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/vspcon for more book information.
 ***/
package com.taobao.ju.no.stm;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Comparable<Account> {
	private int balance;
	public final Lock monitor = new ReentrantLock();

	public Account(final int initialBalance) {
		balance = initialBalance;
	}

	public int compareTo(final Account other) {
		return new Integer(hashCode()).compareTo(other.hashCode());
	}

	public void deposit(final int amount) {
		monitor.lock();
		try {
			if (amount > 0)
				balance += amount;
		} finally { 
			monitor.unlock();
		}
	}

	public boolean withdraw(final int amount) {
		try {
			monitor.lock();
			if (amount > 0 && balance >= amount) {
				balance -= amount;
				return true;
			}
			return false;
		} finally {
			monitor.unlock();
		}
	}
}
