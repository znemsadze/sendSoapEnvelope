package ge.magticom.axis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Properties;

public class SendSoapEnvelope {

	public static String host;
	public static String portst;
	public static String authHeader;
	 static{
	    	Properties props=new Properties();
	    		try {
	    			props.load(new FileInputStream("config.properties"));
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		host=props.getProperty("host");
	    		portst=props.getProperty("port");
	    		authHeader=props.getProperty("authheader");
	    }
	
	
	public static void main(String[] args) throws Exception {

		String path = "envelope";
		File log =new File("log"+Calendar.getInstance().getTimeInMillis());
		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		try {
			PrintWriter out=new PrintWriter(log);
		
		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				out.print(files+"===============================================\r\n");
				out.print(sendEnvelope(readFile(files)));
				System.out.println("---- to "+host+":"+portst+" "+((Integer)i).toString()+" from "+listOfFiles.length + " fileName:"+files);
				out.print("\r\n===============================================\r\n");
			}
		}
		
		out.flush();
    	out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("envelope/"+filename));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();

		} finally {
			br.close();
		}
	}

	public static String sendEnvelope(String xmldata) {

		try {
			// Create socket
			String hostname = host;
			int port=Integer.parseInt(portst);
			InetAddress addr = InetAddress.getByName(hostname);
			Socket sock = new Socket(addr, port);

			// Send header
			String path = "/axis2/services/SpService";
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream(), "UTF-8"));
			// You can use "UTF8" for compatibility with the Microsoft virtual
			// machine.
			wr.write("POST " + path + " HTTP/1.0\r\n");
			wr.write("Host: "+host+":"+port+"\r\n");
			wr.write("Content-Length: " + xmldata.length() + "\r\n");
			wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
			wr.write("Authorization: "+authHeader+"\r\n");
			wr.write("\r\n");

			
			
			// Send data
			wr.write(xmldata);
			wr.flush();

			// Response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder("");
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

}
