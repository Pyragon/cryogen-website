package com.cryo.utils;

public final class Logger {

	public static void handle(Throwable throwable) {
		System.out.println("ERROR! THREAD NAME: " + Thread.currentThread().getName());
		throwable.printStackTrace();
	}

	public static void log(Class<?> c, String message) {
		log(c, message, false);
	}

	public static void log(Class<?> c, String message, boolean err) {
		log(c.getSimpleName(), message, err);
	}

	public static void log(Object classInstance, Object message) {
		log(classInstance.getClass().getSimpleName(), message);
	}

	public static void log(String class_name, Object message) {
		log(class_name, message, false);
	}

	public static void log(String className, Object message, boolean err) {
		String text = "[" + className + "]" + " " + message;
		if (err)
			System.err.println(text);
		else
			System.out.println(text);
	}

	private Logger() {

	}

}
