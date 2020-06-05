
import java.io.* ;
import java.net.* ;
import java.util.* ;

public class WebServer {

	public static void main(String[] argv) throws Exception{	
		
		int port = 0;
		
		if(argv.length<1){
			port = 8080;
		}else{
			port = Integer.parseInt(argv[0]);
		}
		
		//Establish the listen socket.
		ServerSocket Socket_server = new ServerSocket(port);
		System.out.println("Listening for Connection");
		
		//Process HTTP service requests in an infinite loop.
		while(true){
			//Listen for a TCP connection request.
			Socket server = Socket_server.accept();
			
			//Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(server);
			
			//Create a new thread to process the request.
			Thread thread = new Thread(request);
			
			// Start the thread
			thread.start();
			}	
	}
	
	public static class HttpRequest implements Runnable{

		final static String CRLF = "\r\n";
		Socket socket;
		
		//Constructor
		public HttpRequest(Socket socket) throws Exception{
			this.socket = socket;
		}
				
		//Implement the run() method of the Runnable interface.
		public void run(){
			try {
				processRequest();
			} 
			catch (Exception e) {
				System.out.println(e);
			}
		}
		

	private void processRequest() throws Exception{
		// Get a reference to the socket's input and output streams
		InputStream is =  new DataInputStream(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		//Set up input stream filters.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
		//Initialize to Null
		String headerLine = "";
		String requestLine = "";
			
		//Extract headerline and requestline 
		int line_counter=0;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
			line_counter++;
			if(line_counter==1)
				requestLine=headerLine;
		}
			
		//Extract the file name from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();  
		String fileName = tokens.nextToken();
			
		//Append so that file is within current directory
		fileName = "." + fileName;
			
		//opening requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
			
			
		//Construct the response message.
		String statusLine = "";
		String contentTypeLine = "";
		String entityBody = "";
			
		if (fileExists) {
		statusLine = "HTTP/1.0 200 OK" + CRLF;
		contentTypeLine = "Content-type: "+contentType(fileName) + CRLF;
		}else{
		statusLine ="HTTP/1.0 404 NOT FOUND"+CRLF ;
		contentTypeLine = "Content-type:NOT FOUND" + CRLF;
		}
			
		//Send the Status line
		os.writeBytes(statusLine);		
		//Send the content type line.
		os.writeBytes(contentTypeLine);
			
		InetAddress socketInetAddress = socket.getInetAddress();
		String host_Name = socketInetAddress.getHostName();
		os.writeBytes("Connection: Closed" + CRLF + "IP Address: " + host_Name + CRLF);
		String IP_Type = "TCP";
		os.writeBytes("Protocol: "+IP_Type +CRLF);
		String socket_Type = "Connection";
        os.writeBytes("Socket Type: " +socket_Type +CRLF);String socket_Family = "AF_INET";
        os.writeBytes("Socket Family: "+socket_Family +CRLF);  
            
        //end of header line
        os.writeBytes(CRLF);
    	//Entity body
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes("<HTML><TITLE>404 NOT FOUND</TITLE><BODY>UNABLE TO FIND FILE</BODY></HTML>");
		}	
		os.close();
		br.close();
		socket.close();
			
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{	
		//Construct a 1K buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;
				   
		// Copy requested file into the socket's output stream.
		while((bytes = fis.read(buffer))!=-1) {
			os.write(buffer, 0, bytes);
		}
	}

		
	private static String contentType(String fileName){
		//if text/html
		if(fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith(".txt"))
		return "text/html";
		//if gif
		else if(fileName.endsWith(".gif") ) 
		return "image/gif";
		//if jpeg/jpg
		else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) 
		return "image/jpeg";
		//anything else
		return "application/octet-stream";
		}	
	
	}
}

