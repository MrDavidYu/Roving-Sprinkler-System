import java.util.*;
import java.net.*;
import java.io.*;

public class StarMain extends Thread{
	
	private static final int data_port = 4444;
	private static final int command_port = 4445;
	private static final int EMPTY = 2;
	private static final int FULL = 3;
	private static final int REFIL = 0;
	private static final int SPRINKLE = 1;
	
	private Queue<String> refillQueue = new LinkedList<String>();
	
	private synchronized void refillQueueAdd(String str) {
		if(!refillQueue.contains(str)){
			refillQueue.add(str);
		}
	}
	private synchronized String refillQueueRemove() {
		return refillQueue.poll();
	}
	
	public void run() {
		ServerSocket ss = null;
		try {
			//ss = new ServerSocket(command_port);
			while(true){
				ss = new ServerSocket(command_port);
				Socket incomingSocket = null;
				ObjectInputStream in = null;
				incomingSocket = ss.accept();
				in = new ObjectInputStream(incomingSocket.getInputStream());
				String roverIp = (String) in.readObject();
				int status = (int) in.readObject();
				System.out.println("Received status: "+status+" from rover: "+roverIp);
				if (status == EMPTY && !refillQueue.isEmpty()) {  //if non-empty list/contains other requests
					refillQueueAdd(roverIp);
				}else if(status == EMPTY && refillQueue.isEmpty()) {  //if empty list/first rover to request
					refillQueueAdd(roverIp); // Add to queue but also refill right away.
					//ObjectOutputStream out = new ObjectOutputStream(incomingSocket.getOutputStream());
					//out.writeObject(REFIL);
					Socket roverss = new Socket(roverIp, command_port);
					ObjectOutputStream out = new ObjectOutputStream(roverss.getOutputStream());
					out.writeObject(REFIL);
					System.out.println("Sending COMMAND: REFILL");
					out.flush();
					out.close();
					roverss.close();
				}else if(status == FULL) {
					String topRover = refillQueue.peek();
					System.out.println("refillQueue size: "+refillQueue.size());
					if(refillQueue.isEmpty() && !topRover.equals(roverIp)) {
						System.out.println("ERROR!!!!!!!, top rover does not match FULL rover");
						System.out.println(topRover+" "+roverIp);
					}else if(!refillQueue.isEmpty() && topRover.equals(roverIp)) {
						System.out.println("Router sending SPRINKLE COMMAND.");
						topRover = refillQueueRemove();
						Socket serverSocket = new Socket(topRover, command_port);
						ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
						out.writeObject(SPRINKLE);
						out.flush();
						out.close();
						serverSocket.close();
					}else if(refillQueue.isEmpty()){
						System.out.println("Refill Queue Empty: SPRINKLE COMMAND already given!");
					}else{
						System.out.println("ERROR: Unhandled else clause");
					}
					//If there are still items in the list, then directly send CMD to REFILL
					if(!refillQueue.isEmpty()){
						String nextRoverIp = refillQueue.peek();
						Socket serverSocket = new Socket(nextRoverIp, command_port);
						ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
						out.writeObject(REFIL);
						out.flush();
						out.close();
						serverSocket.close();
						System.out.println("Sending REFIL CMD to next rover in queue.");
					}
				} else {
					System.out.println("Unhadled else statement 1");
				}
				
				System.out.println("Refill Queue: ");
				for (String element : refillQueue) { 
					System.out.print(element.toString()+" "); 
				}
				System.out.println("");
				ss.close();
			}
		} catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String args[]) throws Exception {

		float avgTemp = 0;
		float avgHumid = 0;
		
		Thread myThread = new Thread(new StarMain());
		myThread.setDaemon(true);
		myThread.start();
		
		while (true) {
			ServerSocket ss = new ServerSocket(data_port);
			Socket incomingSocket = ss.accept();
			ObjectInputStream in = new ObjectInputStream(incomingSocket.getInputStream());

			String roverIP = (String) in.readObject();
			float roverTemp = (float) in.readObject();
			float roverHumid = (float) in.readObject();
			int powerUsed = (int) in.readObject();
			int waterUsed = (int) in.readObject();
			System.out.println("Temp received " + roverTemp+", humidity received: "+roverHumid+" Water: "+waterUsed+" Power: "+powerUsed);

			// TODO: Cumulative power/water usage

			//SENDING INFO TO THINGSPEAK CLOUD---------------
			Runtime rt = Runtime.getRuntime();
			String[] commands = {"python3", "/home/pi/Desktop/RSS/RSS_thingspeak_router.py", Float.toString(roverTemp), Float.toString(roverHumid), Integer.toString(powerUsed), Integer.toString(waterUsed)};
			Process proc = rt.exec(commands);
	
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
	
			//Read the output from command
			System.out.println("STD OUT from the command:");
			String s = null;
			while((s=stdInput.readLine()) != null){
				System.out.println(s);
			}
			
			System.out.println("STD ERR from the command:");
			while((s=stdError.readLine()) != null){
				System.out.println(s);
			}
			//SENDING INFO TO THINGSPEAK CLOUD---------------

			in.close();
			ss.close();
		}
	}

}
