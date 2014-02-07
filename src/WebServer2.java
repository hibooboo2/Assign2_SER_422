import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import simple.Logger.Logger;

class WebServer2 implements Runnable
{

	/**
	 * The server socket used for this instance of WebServer2.
	 */
	ServerSocket	serverSocket	= null;

	Logger			log				= new Logger(1000, false, 6, true, "WebServer2.log", "Log");

	//
	public static void main(String args[])
	{

		WebServer2 server= null;
		try
		{
			if (args.length != 1)
			{
				System.out.println("Usage: WebServer <port>. \n Defaulting to port: 8080");
				server= new WebServer2(8080);

			}
			else
			{
				System.out.println("Attempting to start server on port: " + Integer.parseInt(args[0]));
				server= new WebServer2(Integer.parseInt(args[0]));

			}
			new Thread(server).start();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			System.out.println("Failed to start server.");
			e.printStackTrace();
		}
	}

	public WebServer2(int port) throws IOException
	{

		this.log.resetLog();
		this.serverSocket= new ServerSocket(port);
		// *** close streams and socket appropriately
		// *** Try to anticipate error conditions (e.g. file not found?)
	}

	@Override
	public void run()
	{

		// TODO Implement Thread Logic From SockServer6
		ExecutorService executor= Executors.newCachedThreadPool();
		while (this.serverSocket.isBound() && !this.serverSocket.isClosed())
		{
			try
			{
				executor.execute(new Connection(this.serverSocket.accept(), this.log));
			}
			catch (IOException e)
			{
				this.log.log(1, "Failed to connect properly.");
			}
			this.log.log(2, "Active threads: " + Thread.activeCount());
		}
	}


	/**
	 * This is a worker class for WebServer2. Merely handles the connection to the server and sends the response.
	 * 
	 * @author Owner
	 * 
	 * @version 0.6
	 * 
	 */
	class Connection implements Runnable
	{

		private Socket	socket;

		private Logger	log;

		private InputStream	in;

		private OutputStream	out;

		Connection(Socket sock, Logger log) throws IOException
		{

			this.log= log;
			this.socket= sock;
			this.in= sock.getInputStream();
			this.out= sock.getOutputStream();
		}
		Connection()
		{

		}

		@Override
		public void run()
		{

			byte[] response= this.createResponse(this.in);
			try
			{
				this.out.write(response);
				this.out.flush();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				this.log.log(1, "Failed to write response.");
			}
			finally
			{
				try
				{
					if (this.socket != null)
					{
						this.socket.close();
					}
					if (this.in != null)
					{
						this.in.close();
					}
					if (this.out != null)
					{
						this.out.close();
					}
				}
				catch (IOException e)
				{
					this.log.log(1, "Something Failed to close.");
					e.printStackTrace();
				}
			}

		}

		/**
		 * Takes and input stream then returns the byte array associated with the file that the input stream specifies.
		 * 
		 * @param inStream
		 * @return The byte array representing the file.
		 */
		public byte[] createResponse(InputStream inStream)
		{

			byte[] response= null;
			BufferedReader in= null;
			try
			{
				// Read from socket's input stream. Must use an
				// InputStreamReader to bridge from streams to a reader
				in= new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
				// Get header and save the filename from the GET line:
				// example GET format: GET /index.html HTTP/1.1
				String filename= null;
				String line= in.readLine();
				this.log.log(3, "Received: " + line);
				if (line != null && !line.trim().equals(""))
				{
					// I will use an artificial delay to test your code
					// Thread.sleep(5000);
					StringTokenizer st= new StringTokenizer(line);
					if (st.nextToken().equals("GET") && st.hasMoreTokens())
					{
						filename= st.nextToken();
						if (filename.startsWith("/"))
						{
							filename= filename.substring(1);
						}
					}
				}
				this.log.log(3, "FINISHED REQUEST, STARING RESPONSE\n");
				// Generate an appropriate response to the user
				if (filename == null)
				{
					response= "<html>Illegal request: no GET</html>".getBytes();
				}
				else
				{
					File file= new File(filename);
					if (!file.exists())
					{
						response= ("<html>File not found: " + filename + "</html>").getBytes();
					}
					else
					{
						response= this.readFileInBytes(file);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				response= ("<html>ERROR: " + e.getMessage() + "</html").getBytes();
			}
			this.log.log(3, "RESPONSE GENERATED!");
			return response;
		}

		/**
		 * Read bytes from a file and return them in the byte array.
		 * We read in blocks of 512 bytes for efficiency.
		 * 
		 * @param f
		 *            The file to be read in.
		 * @return The byte array representing the file.
		 * @throws IOException
		 */
		public byte[] readFileInBytes(File f) throws IOException
		{

			FileInputStream file= new FileInputStream(f);
			ByteArrayOutputStream data= new ByteArrayOutputStream(file.available());
			byte buffer[]= new byte[512];
			int numRead= file.read(buffer);
			while (numRead > 0)
			{
				data.write(buffer, 0, numRead);
				numRead= file.read(buffer);
			}
			file.close();
			byte[] result= data.toByteArray();
			data.close();
			return result;
		}


	}
}