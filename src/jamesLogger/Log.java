package jamesLogger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log
{

	private int			pos			= 0;

	private int			buffer		= 100;

	private String[]	msgs		= new String[this.buffer];

	private boolean		toFile		= false;

	private int			maxLvl		= 3;

	private boolean		tree		= true;

	private String		logFileName	= "LogFile.txt";

	public Log(int logBufferSize, boolean writeToFile, int lvl, boolean tree, String logFileName)
	{

		this.buffer= logBufferSize;
		this.msgs= new String[this.buffer];
		this.toFile= writeToFile;
		this.tree= tree;
		this.maxLvl= lvl;
		this.logFileName= logFileName;
		this.makeFile();
	}

	public Log(boolean writeToFile)
	{

		this.toFile= writeToFile;
		this.makeFile();
	}

	public Log()
	{

		this.msgs= null;
		this.toFile= false;
		this.makeFile();
	}

	public synchronized void log(int lvl, String msg)
	{

		if (!this.toFile)
		{
			if ((this.tree && lvl <= this.maxLvl) || lvl == 1)
			{
				System.out.println(msg);
			}
			else if (!this.tree && lvl == this.maxLvl)
			{
				System.out.println(msg);
			}
		}
		else
		{
			if ((this.tree && lvl <= this.maxLvl) || lvl == 1)
			{
				this.addToLog(msg);
			}
			else if (!this.tree && lvl == this.maxLvl)
			{
				this.addToLog(msg);
			}
		}
	}

	private synchronized void writeToFile()
	{

		File logFile= new File(this.logFileName);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("IOExeception from writing to log. Can't access log file or make one.");
			}
		}
		PrintWriter out= null;
		try
		{
			out= new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
			for (int i= 0; i < this.pos; i++)
			{
				out.println(this.msgs[i]);
			}
			out.flush();
			this.msgs= new String[this.buffer];
			this.pos= 0;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	private synchronized void addToLog(String msg)
	{

		if (this.pos == this.buffer)
		{
			this.writeToFile();
		}
		this.msgs[this.pos]= msg;
		this.pos++;
	}

	public synchronized void flush()
	{

		this.writeToFile();
	}

	public synchronized void resetLog()
	{

		File logFile= new File(this.logFileName);
		logFile.delete();
	}

	private void makeFile()
	{

		File logFile= new File(this.logFileName);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("IOExeception from writing to log. Can't access log file or make one.");
			}
		}
	}
}
