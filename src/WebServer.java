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

class WebServer
{

	//
	public static void main(String args[])
	{

		if (args.length != 1)
		{
			System.out.println("Usage: WebServer <port>");
			System.exit(1);
		}
		WebServer server= new WebServer(Integer.parseInt(args[0]));
	}

	public WebServer(int port)
	{

		ServerSocket server= null;
		Socket sock= null;
		InputStream in= null;
		OutputStream out= null;
		// *** TASK: Open the server socket on the specified port
		// *** Loop forever accepting socket requests
		// *** Process each request in its own thread
		// *** Get the response bytes from createResponse
		// *** Write the bytes to the socket's output stream
		// *** close streams and socket appropriately
		// *** Try to anticipate error conditions (e.g. file not found?)
	}

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
			System.out.println("Received: " + line);
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
			System.out.println("FINISHED REQUEST, STARING RESPONSE\n");
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
		System.out.println("RESPONSE GENERATED!");
		return response;
	}

	/**
	 * Read bytes from a file and return them in the byte array.
	 * We read in blocks of 512 bytes for efficiency.
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
