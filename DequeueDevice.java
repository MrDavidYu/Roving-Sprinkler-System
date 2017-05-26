import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DequeueDevice {

	private static final int command_port = 4445;
	private static final String routerIP = "192.168.1.1";
	private static final String myIP = "192.168.1.100";
	private static final int EMPTY = 2;
	private static final int FULL = 3;
	
	public static void main(String[] args) {
		Socket serverSocket;
		try {
			serverSocket = new Socket(routerIP, command_port);
			ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
			out.writeObject(myIP);
			out.writeObject(FULL);
			out.flush();

			out.close();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
