package com.cryo.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.cryo.tasks.impl.Task;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 2:56:11 PM
 */
public class TaskManager extends Thread {
	
	private static long TIME = 1000;
	
	private ArrayList<Task> tasks;
	
	public TaskManager() {
		setName("Task Manager");
		setPriority(Thread.MAX_PRIORITY);
		load();
	}
	
	public void load() {
		tasks = new ArrayList<>();
		try {
			for(Class<?> c : Utilities.getClasses("com.cryo.tasks.impl")) {
				if(c.isAnonymousClass())
					continue;
				if(c.getSimpleName().equals("Task"))
					continue;
				System.out.println("Loaded: "+c.getName());
				tasks.add((Task) c.newInstance());
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			long start = System.currentTimeMillis();
			Calendar c = Calendar.getInstance();
			int second = c.get(Calendar.SECOND);
			int minute = c.get(Calendar.MINUTE);
			int hour = c.get(Calendar.HOUR);
			for(Task task : tasks) {
				if(task.getHour() != hour && task.getHour() != -1)
					continue;
				if(task.getMinute() != minute && task.getMinute() != -1)
					continue;
				if(task.getSecond() != second && task.getSecond() != -1)
					continue;
				task.run();
			}
			try {
				long passed = System.currentTimeMillis()-start;
				Thread.sleep(TIME-passed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
