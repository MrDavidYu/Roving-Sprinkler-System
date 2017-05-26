import java.util.*;
import java.net.*;
import java.io.*;

public class StarDevice extends Thread {

	private static final int REFIL = 0;
	private static final int SPRINKLE = 1;
	private static final int data_port = 4444;
	private static final int command_port = 4445;
	private static final String routerIP = "192.168.1.1";
	private static final String myIP = "192.168.1.102";
	private static int STATUS = SPRINKLE;

	public static void main(String[] args) throws Exception {
		Thread myThread = new Thread(new StarDevice());
		myThread.setDaemon(true);
		myThread.start();
		Runtime.getRuntime().exec("python3 /home/pi/Desktop/joystick.py");
		while (true) {
			if (STATUS == REFIL) {
				// Spins
				
			} else if (STATUS == SPRINKLE) {
				
				Process p1 = Runtime.getRuntime().exec("python3 /home/pi/Desktop/sense_hat_temp_star.py");
				BufferedReader bufIn_temp = new BufferedReader(new InputStreamReader(p1.getInputStream()));
				String pythonOutput_temp = bufIn_temp.readLine();
				float currTemp = Float.parseFloat(pythonOutput_temp);

				Process p2 = Runtime.getRuntime().exec("python3 /home/pi/Desktop/sense_hat_humid_star.py");
				BufferedReader bufIn_humid = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				String pythonOutput_humid = bufIn_humid.readLine();
				float currHumid = Float.parseFloat(pythonOutput_humid);

				//Get power usage readings and write to outstream
				Runtime rt = Runtime.getRuntime();
				String[] commands = {"python3", "/home/pi/Desktop/detect_power.py"};
				Process proc = rt.exec(commands);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String s_power = stdInput.readLine();
				
				//Get water usage readings and write to outstream
				Runtime rt2 = Runtime.getRuntime();
				String[] commands2 = {"python3", "/home/pi/Desktop/detect_water.py"};
				Process proc2 = rt2.exec(commands2);
				BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(proc2.getInputStream()));
				String s_water = stdInput2.readLine();

				System.out.println("Sending curr temperature: " + currTemp + " curr humidity: " + currHumid + " power usage:" + s_power + " water usage:" + s_water);
				Runtime.getRuntime().exec("python3 /home/pi/Desktop/data_transmitted.py");

				Socket serverSocket = new Socket(routerIP, data_port);
				ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
				out.writeObject(myIP);
				out.writeObject(currTemp);
				out.writeObject(currHumid);
				out.writeObject(Integer.parseInt(s_power));
				out.writeObject(Integer.parseInt(s_water));
				out.flush();

				out.close();
				serverSocket.close();
			}
			Thread.sleep(5000);

		}
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		Socket incomingSocket = null;
		ObjectInputStream in = null;
		System.out.println("StarDevice STATUS_CHECK thread running");
		try {
			while (true) {
				serverSocket = new ServerSocket(command_port);
				incomingSocket = serverSocket.accept();
				Runtime.getRuntime().exec("python3 /home/pi/Desktop/data_received.py");
				System.out.println("Accepted COMMAND socket connection request from Router.");
				in = new ObjectInputStream(incomingSocket.getInputStream());
				STATUS = (int) in.readObject();
				if(STATUS == SPRINKLE){
					Runtime.getRuntime().exec("python3 /home/pi/Desktop/sprinkle.py");
				} else if(STATUS == REFIL) {
					Runtime.getRuntime().exec("python3 /home/pi/Desktop/refill.py");
				} else {
					System.out.println("ERROR: Unhandled else statement with STATUS: "+STATUS);
				}
				serverSocket.close();
				incomingSocket.close();
				in.close();
				System.out.println("STATUS COMMAND:"+STATUS+" from Router received Successfully.");
			}
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
