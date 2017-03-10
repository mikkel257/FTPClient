import FTPClient.ConnectionException;

public class Main {

	public static void main(String[] args) {
		FTPClient client = new FTPClient();
		try 
		{
		client.connect();
		}
		catch (ConnectionException e)
		{
			System.out.println(e.getMessage());
		}
	}

}
