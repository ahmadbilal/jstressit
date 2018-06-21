// JStressIt by Ahmad Bilal - a simple Java stress testing tool

import java.io.*;
import java.lang.reflect.*;

public class JStressIt
{
	public static void main(String [] args)
	{
		try
		{
			System.out.println("JStressIt version 1.0");
			if(args.length != 3)
			{
				System.out.println("Usage: java JStressIt.class <Test Class> <Number of Threads per Test> <Stats Interval in seconds>");
				return;
			}
			String tstName = args[0];
			Integer n = Integer.parseInt(args[1]);
			Integer t = Integer.parseInt(args[2]);
			int numThreads = n.intValue();
			int perfTick = t.intValue();
			System.out.println("Loading test class: " + tstName + " with " + n + " threads per test ...");
			Class<?> tstClass = Class.forName(tstName);
			Method [] tstMethods = tstClass.getDeclaredMethods();
			for(int i = 0; i < tstMethods.length; i++)
			{
				System.out.println("Initiating " + n + " Threads for Test: " + tstMethods[i].getName());
				LauncherThread tstLaunch = new LauncherThread(tstClass, tstMethods[i], numThreads, perfTick);
				new Thread(tstLaunch).start();
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

class LauncherThread implements Runnable
{
	Class tstClass;
	Method tstMethod;
	int numThreads;
	int perfTick;
	public double perfCount;
	LauncherThread(Class c, Method m, int n, int p)
	{
		tstClass = c;
		tstMethod = m;
		numThreads = n;
		perfCount = 0;
		perfTick = p;
	}
	
	public void run()	//Start test threads and start counting
	{
		try
		{
			for(int i = 0; i < numThreads; i++)
			{
				TestThread tThread = new TestThread(tstClass, tstMethod, this);
				new Thread(tThread).start();
			}
			while(true)
			{
				Thread.sleep(perfTick * 1000);
				perfCount = perfCount / (double)perfTick;
				System.out.println("RPS for Test " + tstMethod.getName() + ": " + perfCount);
				perfCount = 0;
			}

		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
}

class TestThread implements Runnable
{
	Object tstObject;
	Class <?> tstClass;
	Method tstMethod;
	LauncherThread tstLauncher;
	TestThread(Class c, Method m, LauncherThread l) throws Exception
	{
		tstClass = c;
		tstMethod = m;
		tstLauncher = l;
	}
	public void run()	//Start test thread
	{
		try
		{
			while(true)
			{
				tstObject = tstClass.getConstructor().newInstance();
				tstMethod.invoke(tstObject);
				tstLauncher.perfCount++;
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
}