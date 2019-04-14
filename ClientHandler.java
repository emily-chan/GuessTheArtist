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
import java.util.Collections;



public class ClientHandler implements Runnable {
  private Socket connectionSock = null;
  private ArrayList<Socket> socketList;
  public static ArrayList<Player> playerList = new ArrayList<Player>();
  public String artist = "";

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

      String username = clientInput.readLine();   // 1 //
      if(username.equalsIgnoreCase("host")) {
        // whenever another user joins the chat, display to all clients and server
        artist = clientInput.readLine(); // 2 //
      }
      else {  //if not host
        String text = clientInput.readLine();
        if (text.equalsIgnoreCase("join game")) { // 3 //
          Player temp = new Player(username);
          temp.setOptedIn(true);
          playerList.add(temp);
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes(username + " has joined the game!\n");
            }
          }
        }
        else {
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes(text + "\n");
            }
          }
        }
      }
      while (true) {
          if (username.equalsIgnoreCase("host")) {
            String command = clientInput.readLine(); // 4 //
            if (command.equalsIgnoreCase("points")) {
              String userToaward = clientInput.readLine();
              int pts = clientInput.read();
              for (Player p : playerList) {
                if (p.getUsername().equalsIgnoreCase(userToaward)) {
                  p.setScore(p.getScore() + pts);
                }
              }
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("points awarded: " + pts + "\n");

                }
              }


            } else if (command.equalsIgnoreCase("leaderboard")) {
            //  String leaderboard = clientInput.readLine(); // 7 //
              Collections.sort(playerList, Collections.reverseOrder());

              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("LEADERBOARD: \n");
                  for (Player p : playerList) {
                    if (p.getOptedIn()) {
                      clientOutput.writeBytes(p.toString() + "\n"); // 7 //
                    }
                  }
                }
              }
            }
            else {  //if none of key words entered, then broadcast
              String input = clientInput.readLine();
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(input+"\n");
                }
              }
            }
          }
          else {  //if not host client
            String input = clientInput.readLine();
            if (input.equalsIgnoreCase("join game")) { // 3 //
              Player temp = new Player(username);
              temp.setOptedIn(true);
              playerList.add(temp);
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(username + " has joined the game!\n");
                }
              }
            }
            else {
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(input+"\n");
                }
              }
            }
          }


        /* else {
          // Connection was lost
          System.out.println("Closing connection for socket " + connectionSock);
          // Remove from arraylist
          socketList.remove(connectionSock);
          connectionSock.close();
          break;
        }
        */
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.toString());
      // Remove from arraylist
      socketList.remove(connectionSock);
    }
  }
} // ClientHandler for MtServer.java
