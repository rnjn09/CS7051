import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
	
	private ServerSocket serverSocket;
	private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	
	//Constructor
	public Server(ServerSocket serverSocket){
		this.serverSocket = serverSocket;
	}
	
	//Server Start
	public void start(){
		try{
			while(true){
				if(executorService.getActiveCount() < executorService.getMaximumPoolSize()){
					Socket socket = serverSocket.accept();
					Server.executorService.execute(new ClientWorker(socket, this));
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public class ClientWorker implements Runnable {
		
		private Socket socket;
		private String command;
		private Server server;
		private boolean killServer;
		
		//Constructor
		public ClientWorker(Socket socket, Server server){
			this.socket = socket;
			this.server = server;
			this.killServer = false;
		}
		
		//Expected Output
		public String heloResponse(){
			return command + "\nIP:" + socket.getLocalAddress().toString().substring(1) + "\nPort:" + socket.getLocalPort() + "\nStudentID:15314217\n";  
		}
		
		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				while(!killServer){
					command = reader.readLine();
					if(command.startsWith("HELO")){
						writer.println(heloResponse());
					}
					else if(command.equals("KILL_SERVICE")){
						killServer = true;
						socket.close();
						server.killService();
					}
					else{
						//Do Nothing
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	//Killing the Server
	public void killService(){
		try {
			executorService.shutdownNow();
			serverSocket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Main method
	public static void main(String[] args) throws IOException {
		int portNumber = Integer.parseInt(args[0]);
		Server server = new Server(new ServerSocket(portNumber));
		server.start();
	}
	
}