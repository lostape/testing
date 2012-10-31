import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class WebServer implements Runnable {
	
	Socket mysocket;

	WebServer(Socket requester){
		this.mysocket = requester;
	}

	public void run() {
		
		try {
			
			InputStream in = mysocket.getInputStream();
			OutputStream out = mysocket.getOutputStream();
			
			Scanner request = new Scanner(in);
			//Processing the GET requests to get path requested; checking if request is valid
			String path = "";
			while(request.hasNextLine()){
				String line = request.nextLine();
				
				if(line.split(" ")[0].contains("GET") && (line.split(" ")[2].contains("HTTP/1.0" ) || line.split(" ")[2].contains("HTTP/1.1" ))){
					path = line.split(" ")[1];
					//System.out.println(line);
					break;
				}
				else{
					out.write("HTTP/1.0 400 BAD REQUEST\r\n\r\n".getBytes());
					mysocket.close();
					return;
				}
				
			}
			
			String simple = "";
			File file = new File("." + path);

			if(file.exists()){
				Scanner page = new Scanner(new FileInputStream(file));
				
				out.write("HTTP/1.0 200 OK\r\n\r\n".getBytes());
				//Building simplified page
				while(page.hasNextLine()){
					String fileline = page.nextLine();
					//If line is a link and if they contain files that have been downloaded then keep them.
					if(!fileline.contains("href")){
						//fileline = page.nextLine();
					}
					else{
						String link = fileline.split("\"")[1];
						if(new File("./" + link).exists()){
							simple += fileline;
						}
					}
					
				}
				out.write(simple.getBytes());
				page.close();
			}
			else{
				out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
			}
			
			mysocket.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
	}


	public static void main(String[] args) throws NumberFormatException, IOException {
		
		ServerSocket server = new ServerSocket(Integer.valueOf(args[0]));
		
		while(true){
			Socket sock = server.accept();
			new Thread(new WebServer(sock)).start();
		}
		
		
		

	}
	
}
