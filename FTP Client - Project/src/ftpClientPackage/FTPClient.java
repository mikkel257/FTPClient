package ftpClientPackage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FTPClient {

	//Instance variables
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	
	/**
	 * Connects the FTP client to the FTP server.
	 * @throws ConnectionException If something went wrong when trying to connect to the FTP server.
	 */
	public void connect() throws ConnectionException
	{
		//The text which contains which information is required from the user to connect to the FTP server.
		String[] messagesToUser = {"Please enter the IP of the FTP-server that you wish to connect to: ",
								   "Please enter the port of the FTP-server that you wish to connect to: "};
		
		//Getting the information to connect to the FTP server from the user.
		String[] connectInformation = getUserInput(messagesToUser);
		
		//Trying to create a socket which speaks with the FTP server socket.
		try
		{
		clientSocket = new Socket(connectInformation[0], Integer.parseInt(connectInformation[1]));
		}
		catch (NumberFormatException e)
		{
			throw new ConnectionException("The port has to be an integer to connect to that IP", e);
		}
		catch (UnknownHostException e)
		{
			throw new ConnectionException("Could not find the entered IP", e);
		}
		catch (IOException e)
		{
			throw new ConnectionException("Some sort of IOException occured when trying to create the socket.", e);
		}

		// Trying to get the newly created sockets output stream.
		try
		{
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		}
		catch (IOException e)
		{
			throw new ConnectionException("Some sort of IOException occured when trying to get the sockets output stream", e);
		}
		
		// Trying to get the newly created sockets input stream.
		try
		{
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		catch (IOException e)
		{
			throw new ConnectionException("Some sort of IOException occured when trying to get the sockets input stream" , e);
		}
		
	}
	
	
	/**
	 * Returns an array of user input.
	 * @param messagesToUser The messages that specifies what user input is needed.
	 * @return A String array of user inputs.
	 */
	public static String[] getUserInput(String[] messagesToUser)
	{
		Scanner keyboard = new Scanner(System.in);
		String[] userInput = new String[messagesToUser.length];
		
		for (int messageNr = 0; messageNr < messagesToUser.length; messageNr++)
		{
			System.out.println(messagesToUser[messageNr]);
			userInput[messageNr] = keyboard.nextLine();
		}
		keyboard.close();
		return userInput;
	}
	
	/**
	 * 
	 * @author Mikkel Holmbo Lund - Denmark Technical University
	 * This exception class is used to in the connect method to show and display the connection problems that might be.
	 */
	public class ConnectionException extends Exception {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 * 
		 * @param msg
		 *            The error message to pass on.
		 * @param e
		 *            The exception to pass on.
		 */
		public ConnectionException(String msg, Throwable e) {
			super(msg, e);
		}

		/**
		 * Constructor.
		 * 
		 * @param msg
		 *            The error message to pass on.
		 */
		public ConnectionException(String msg) {
			super(msg);
		}
	}
}
