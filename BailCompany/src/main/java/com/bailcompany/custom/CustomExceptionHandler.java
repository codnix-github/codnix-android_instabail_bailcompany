package com.bailcompany.custom;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler
{

	private UncaughtExceptionHandler defaultUEH;

	public CustomExceptionHandler()
	{
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		Log.d("TAG", "stacktrace: \n"+stacktrace);
		defaultUEH.uncaughtException(t, e);
	}
}