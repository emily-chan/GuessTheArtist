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

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ClientHandler implements Runnable {
  private Socket connectionSock = null;
  private ArrayList<Socket> socketList;
  public static ArrayList<Player> playerList = new ArrayList<Player>();
  public String artist = "";
  public String hint = "";

  ClientHandler(Socket sock, ArrayList<Socket> socketList) {
    this.connectionSock = sock;
    this.socketList = socketList;  // Keep reference to master list
  }

  public void run() {
    try {
      System.out.println("Connection made with socket " + connectionSock);
      BufferedReader clientInput = new BufferedReader(
          new InputStreamReader(connectionSock.getInputStream()));

      String username = clientInput.readLine();
      if(username.equalsIgnoreCase("host")) {
      	artist = clientInput.readLine();
      } else {  //if not host
        String text = clientInput.readLine();
        if (text.equalsIgnoreCase("join game")) {
          Player temp = new Player(username);
          temp.setOptedIn(true);
          playerList.add(temp);
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes(username + " has joined the game!\n");
            }
          }
        } else {
          for (Socket s : socketList) {
            if (s != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
              clientOutput.writeBytes(username + ": " + text + "\n");
            }
          }
        }
      }
      while (true) {
        String command = clientInput.readLine();
        if(command != null) {
          if (username.equalsIgnoreCase("host")) {
            if (command.equalsIgnoreCase("points")) {
              String user = clientInput.readLine();
              int pts = Pythonreader.runAnalysis(artist);
              System.out.println("sentiment analysis result: " + pts);
              //int pts = clientInput.read();
              boolean playerOptedIn = false;
              for (Player p : playerList) {
                if (p.getUsername().equalsIgnoreCase(user)) {
                  p.setScore(p.getScore() + pts);
                  playerOptedIn = true;
                }
              }
              if (playerOptedIn) {
                for (Socket s : socketList) {
                  if (s == connectionSock) {
                    DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                    clientOutput.writeBytes("\n" + user + " was awarded " + pts + " points\n");
                  }
                }
              } else {
                for (Socket s : socketList) {
                  if (s == connectionSock) {
                    DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                    clientOutput.writeBytes("\n" + user + " has not opted into the game! No points awarded\n");
                  }
                }
              }
            } else if (command.equalsIgnoreCase("leaderboard")) {
                Collections.sort(playerList, Collections.reverseOrder());
                for (Socket s : socketList) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("LEADERBOARD: \n");
                  for (Player p : playerList) {
                    if (p.getOptedIn()) {
                      clientOutput.writeBytes(p.toString() + "\n");
                    }
                  }
                }
            } else if (command.equalsIgnoreCase("new artist")) {
              String newArtist = clientInput.readLine();
              artist = newArtist;
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("Host has changed the artist!\n");
                }
              }
            } else if (command.equalsIgnoreCase("start game")) {
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("Host has started the game!\n");
                }
              }
            /* } else if (command.equalsIgnoreCase("hint")) {
               for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes("Host has given a hint!\n");
                }
            } */
              //retrieve song from spotify
              //playback song
              //start timer, maybe give hints after certain time periods
            } else {  //if none of key words entered, then broadcast
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(username + ": " + command + "\n");
                }
              }
            }
          } else {  //if not host client
            if (command.equalsIgnoreCase("join game")) {
              Player temp = new Player(username);
              temp.setOptedIn(true);
              playerList.add(temp);
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(username + " has joined the game!\n");
                }
              }
            } else if (command.equals("hint")) {
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(username + " requested a hint\n");
                }
              }
            } else {
              for (Socket s : socketList) {
                if (s != connectionSock) {
                  DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
                  clientOutput.writeBytes(username + ": " + command + "\n");
                }
              }
            }
          }
        } else {
          //Connection was lost
          System.out.println("Closing connection for socket " + connectionSock);
          //Remove from arraylist
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
