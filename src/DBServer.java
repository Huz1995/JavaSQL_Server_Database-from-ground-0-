import java.io.*;
import java.net.*;

import exceptionPackage.ErrorException;
import parserPackage.DbState;
import parserPackage.Parser;

class DBServer
{
    public DBServer(int portNumber)
    {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) processNextConnection(serverSocket);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextConnection(ServerSocket serverSocket)
    {
        try {
            Socket socket = serverSocket.accept();
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connection Established");
            while(true) processNextCommand(socketReader, socketWriter);
        } catch(IOException ioe) {
            System.err.println(ioe);
        } 
        catch(NullPointerException npe) {
            System.out.println("Connection Lost");
        }
    }

    private void processNextCommand(BufferedReader socketReader, BufferedWriter socketWriter) throws IOException, NullPointerException
    {
        String incomingCommand = socketReader.readLine();
        System.out.println("Received message: " + incomingCommand);
        try {
            DbState response = new DbState();
            Parser parser = new Parser(incomingCommand, response);
            socketWriter.write(response.getClientResponse());
        } catch(ErrorException exception) {
            socketWriter.write(exception.toString());
        } catch(IOException exception) {
            socketWriter.write(exception.toString());
        }
        socketWriter.write("\n" + ((char)4) + "\n");
        socketWriter.flush();
        

    }

    public static void main(String args[])
    {
        DBServer server = new DBServer(8888);
    }

}