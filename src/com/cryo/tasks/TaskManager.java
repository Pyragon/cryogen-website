package com.cryo.tasks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimerTask;

import com.cryo.Website;
import com.cryo.tasks.impl.Task;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: March 13, 2017 at 2:56:11 PM
 */
public class TaskManager extends TimerTask {

    private static long TIME = 1000;

    private static ArrayList<Task> tasks;

    public TaskManager() {
        load();
    }

    public void load() {
        tasks = new ArrayList<>();
        try {
            for (Class<?> c : Utilities.getClasses("com.cryo.tasks.impl")) {
                if (c.isAnonymousClass())
                    continue;
                if (c.getSimpleName().equals("Task"))
                    continue;
                Task task = (Task) c.getConstructor().newInstance();
                tasks.add(task);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
	}
	
	public void run() {
		if (!Website.LOADED)
			return;
		Calendar c = Calendar.getInstance();
		int second = c.get(Calendar.SECOND);
		int minute = c.get(Calendar.MINUTE);
		int hour = c.get(Calendar.HOUR);
        int day = c.get(Calendar.DAY_OF_MONTH);
		for (Task task : tasks) {
            String[] times = task.getTime().split(" "); //0 - day, 1 - hour, 2 - minute, 3 - second
            if(!checkTime(times[0], day)) continue;
            if(!checkTime(times[1], hour)) continue;
            if(!checkTime(times[2], minute)) continue;
            if(!checkTime(times[3], second)) continue;
			task.run();
		}
	}

    public boolean checkTime(String time, int actualTime) {
        if(time.equals("*")) return true;
        if(time.startsWith("%")) {
            int mod = Integer.parseInt(time.substring(1));
            if(actualTime % mod == 0) return true;
            return false;
        }
        return Integer.parseInt(time) == actualTime;
    }
	
}
