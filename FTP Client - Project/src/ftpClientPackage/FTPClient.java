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
	public boolean connect() throws ConnectionException
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
		
		if (isConnected())
		{
			String line = getNextServerMessage();
			if (isExpectedAnswer(getReturnCode(line), "220"))
			{
				System.out.println(line);
			}
			return true;
		}
		else
		{
			System.out.println("Could not connect to the FTP server");
			return false;
		}
	}
	
	//___________________________________________________________________________________________
	// Secondary methods
	//___________________________________________________________________________________________
	
	/**
	 * Returns an array of user input.
	 * @param messagesToUser The messages that specifies what user input is needed.
	 * @return A String array of user inputs.
	 */
	private String[] getUserInput(String[] messagesToUser)
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
	 * Returns a string containing the next server response.
	 * @return The next message from the server.
	 * @throws ConnectionException If something went wrong while trying to retrieve the next server response.
	 */
	private String getNextServerMessage() throws ConnectionException {
		try 
		{
			return inFromServer.readLine();
		} 
		catch (IOException e) 
		{
			throw new ConnectionException("Some sort of IOException occured which include that the connection to the server may have been lost",
					e);
		}
	}
	
	/**
	 * Returns a string containing the next return code from the server.
	 * @return The next return code from the server.
	 * @throws ConnectionException If something went wrong while trying to retrieve the next server return code.
	 */
	private String getReturnCode(String s) throws ConnectionException
	{
		return s.substring(0,3);
	}
	
	private boolean isConnected()
	{
		return clientSocket.isConnected();
	}
	
	private boolean isExpectedAnswer(String answer, String expected)
	{
		if (answer.equals(expected))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 
	 * @author Mikkel Holmbo Lund - Denmark Technical University
	 * This exception class is used to in the connect method to show and display the connection problems that might be.
	 * This class is an inner class of FTPClient.
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
