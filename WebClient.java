
import java.io.* ;
import java.net.* ;

public class WebClient {
	
	final static String CRLF = "\r\n";
	
	public static void main(String args[]) throws Exception{			
		
		String ip_address = args[0];
		int port_id = Integer.parseInt( args[1] );
		String file_name = args[2];
				
		//Response buffer
		String response_buffer = "";
				
		Socket Socket_client = new Socket(ip_address, port_id);
		    	
		PrintWriter print_writer = new PrintWriter(Socket_client.getOutputStream());
		        
		BufferedReader br = new BufferedReader(new InputStreamReader(Socket_client.getInputStream()));
		//Start Time
		long startTime = System.currentTimeMillis();
		        
		//Flush from buffer
		print_writer.print("Client: "+"GET /"+file_name+" HTTP/1.0"+CRLF+" Host: "+ip_address+":"+port_id+CRLF+" User-Agent:Command Prompt "+"Accept: text/html,application/xhtml+xml,application/xml"+CRLF+"Accept-Language: en"+"Accept-Encoding: gzip, deflate"+CRLF+"Connection: keep-alive"+CRLF+CRLF);
		print_writer.flush();
		        
		//Response buffer
		while( (response_buffer = br.readLine()) != null){
			System.out.println(response_buffer);    
		}
		        
		// To Calculate Round trip time
		long endTime   = System.currentTimeMillis();
		        
		long totalTime = endTime - startTime;
		        
		System.out.println("The Round Trip Time is: "+totalTime+"ms");
				
	}
}