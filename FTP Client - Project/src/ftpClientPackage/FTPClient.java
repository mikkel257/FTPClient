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
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	private Scanner scanner;
	
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

		//Trying to retrieve the output and input streams of the newly created socket.
		try
		{
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		catch (IOException e)
		{
			throw new ConnectionException("Some sort of IOException occured when trying to get the sockets output stream or input stream", e);
		}
		
		//If connected print return code and a message.
		if (isConnected())
		{
			System.out.println("You have succesfully connected to FTP-server on IP: " + connectInformation[0] + " on port: " + connectInformation[1] + "\n");
			String line = getNextServerMessage();
			if (isExpectedAnswer(getReturnCode(line), "220"))
			{
				System.out.println( getReturnCode(line) + " Service ready for new user. \n");
			}
			return true;
		}
		else
		{
			System.out.println("Could not connect to the FTP server");
			return false;
		}
	}
	
	/**
	 * Login to the connected FTP server.
	 * @return true if logged in, false otherwise.
	 */
	public boolean login()
	{
		boolean isLoggedIn = false;
		String username = null;
		String password = null;
		sendInitialCommand();
		try 
		{	
			//Expected message first time: "530 Please login with USER and PASS."
			//Expected message afterwards: "530 Login incorrect."
			String message = getNextServerMessage();
			
			if (isExpectedAnswer(getReturnCode(message), "530"))
			{
				System.out.println(message);
				username = getUserInput("Please Enter your username: ");
				sendCommand("USER " + username);
				System.out.println();
				message = getNextServerMessage();
				
				// Expected message "331 Please specify password."
				if (isExpectedAnswer(getReturnCode(message), "331"))
				{
					System.out.println(message);
					password = getUserInput("Please Enter your password: ");
					sendCommand("PASS " + password);
					System.out.println();
					message = getNextServerMessage();
					
					// Expected message "230 Login succesful."
					if (isExpectedAnswer(getReturnCode(message), "230"))
					{
						System.out.println(message);
						isLoggedIn = true;
					}
					else
					{
						System.out.println("[ERROR] Wrong password. Please try to login again.\n");
						isLoggedIn = login();
					}
				}
				else
				{
					System.out.println("[ERROR] No such user available.");
				}
			}
			else
			{
				System.out.println("[ERROR] Failed to login. Closing the connection.");
			}
		} 
		catch (ConnectionException e) 
		{
			System.out.println(e);
		}
		return isLoggedIn;
	}
	
	/**
	 * Sends initial command. This is needed to be told that you need to login with USER and PASS.
	 */
	public void sendInitialCommand()
	{
		//Sends some initial command to the FTP server.
		sendCommand("Hello");
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
		String[] userInput = new String[messagesToUser.length];
		scanner = new Scanner(System.in);
		for (int messageNr = 0; messageNr < messagesToUser.length; messageNr++)
		{
			System.out.print(messagesToUser[messageNr]);
			userInput[messageNr] = scanner.nextLine();
			System.out.println();
			
		}
		return userInput;
	}
	
	/**
	 * Returns a string with the user input
	 * @param messageToUser The message that specifies what user input is needed.
	 * @return A string containing the user input.
	 */
	private String getUserInput(String messageToUser)
	{
		System.out.print(messageToUser);
		scanner = new Scanner(System.in);
		return scanner.nextLine();
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
	
	/**
	 * Returns true if the socket is connected.
	 * @return true if connected false otherwise.
	 */
	private boolean isConnected()
	{
		return clientSocket.isConnected();
	}
	
	/**
	 * Returns true if the answer is the expected otherwise false.
	 * @param answer The answer.
	 * @param expected The expected answer.
	 * @return True if the answer is equal to the expected answer, false otherwise.
	 */
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
	 * Sends a command to the FTP server, remember to add \n at the end of the command.
	 * @param command The command to be send.
	 */
	private void sendCommand(String command)
	{
		try 
		{
			if (isConnected())
			{
			outToServer.writeBytes(command + "\n");
			}
			else
			{
				System.out.println("You may have lost the connection to the FTP server.");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Some sort of IOException occured when trying to send a command. Closing the client connection");
			try 
			{
				clientSocket.close();
			} 
			catch (IOException e1) {
				
				System.out.println("Could not close the socket.");
			}
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
		
		@Override
		public String toString()
		{
			return "ConnectionException: " + super.getMessage();
		}
	}
	
	
}
