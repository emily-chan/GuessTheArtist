/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Scanner;


public class ClientHandler implements Runnable {
  private Socket connectionSock = null;
  private ArrayList<Socket> socketList;

  ClientHandler(Socket sock, ArrayList<Socket> socketList) {
    this.connectionSock = sock;
    this.socketList = socketList;  // Keep reference to master list
  }

  /**
   * received input from a client.
   * sends it to other clients.
   */
  public void run() {
    try {
      System.out.println("Connection made with socket " + connectionSock);
      BufferedReader clientInput = new BufferedReader(
          new InputStreamReader(connectionSock.getInputStream()));

      String username = clientInput.readLine();
      System.out.println(username + " has joined the chat");
      // whenever another user joins the chat, display to all clients and server

      if (username.equalsIgnoreCase("host")) {
        String question = clientInput.readLine();
        //System.out.println("Question from Host: " + question);
        for (Socket s : socketList) {
          if (s != connectionSock) {
            DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
            clientOutput.writeBytes(username + ": " + question + "\n");
          }
        }
        String command = clientInput.readLine();
        if (command.equalsIgnoreCase("add")) {
          String add = clientInput.readLine();
          System.out.println("added client");
        } else if (command.equalsIgnoreCase("points")) {
          String points = clientInput.readLine();
          System.out.println("assigned points");
        } else if (command.equalsIgnoreCase("leaderboard")) {            
          String leaderboard = clientInput.readLine();
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes("LEADERBOARD: \n");
              clientOutput.writeBytes(leaderboard);
            }
          }
        }
      }

      while (true) {
        // Get data sent from a client
        String clientText = clientInput.readLine();
        if (clientText != null) {
          System.out.println(username + ": " + clientText);
          // Turn around and output this data
          // to all other clients except the one
          // that sent us this information
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes(username + ": " + clientText + "\n");
            }
          }
        } else {
          // Connection was lost
          System.out.println("Closing connection for socket " + connectionSock);
          // Remove from arraylist
          socketList.remove(connectionSock);
          connectionSock.close();
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.toString());
      // Remove from arraylist
      socketList.remove(connectionSock);
    }
  }
} // ClientHandler for MtServer.java