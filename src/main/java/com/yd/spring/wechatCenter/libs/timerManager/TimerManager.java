package com.yd.spring.wechatCenter.libs.timerManager;
import org.apache.commons.lang.RandomStringUtils;

import java.util.HashMap;
import java.util.Timer;

public class TimerManager extends Timer {
	private HashMap<String, TimerCallback> timeoutTaskList  = new HashMap<>();
	private HashMap<String, TimerCallback> intervalTaskList = new HashMap<>();

	public TimerManager() {
		super(true);
	}

	public String setTimeout(TimerCallback timerTask, Long millisecond) {
		String taskId = generateRandomId();
		timeoutTaskList.put(taskId, timerTask);
		super.schedule(timerTask, millisecond);
		return taskId;
	}

	public void clearTimeout(String id) {
		timeoutTaskList.get(id).cancel();
	}

	public String setInterval(TimerCallback timerTask, Long millisecond) {
		String taskId = generateRandomId();
		intervalTaskList.put(taskId, timerTask);
		super.scheduleAtFixedRate(timerTask, millisecond, millisecond);
		return taskId;
	}

	public void clearInterval(String id) {
		intervalTaskList.get(id).cancel();
	}

	private String generateRandomId() {
		return RandomStringUtils.randomAlphanumeric(7);
	}
}
