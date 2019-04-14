/**
 * MTClient.java
 *
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * Anything sent to the server is broadcast to all clients.
 *
 * The MTClient uses a ClientListener whose code is in a separate file.
 * The ClientListener runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;

public class MtClient {
  /**
   * main method.
   * @params not used.
   */


  public static void main(String[] args) {
    try {
      String hostname = "localhost";
      int port = 7654;

      System.out.println("Connecting to server on port " + port);
      Socket connectionSock = new Socket(hostname, port);

      DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());

      System.out.println("Connection made.");

      // Start a thread to listen and display data sent by the server
      ClientListener listener = new ClientListener(connectionSock);
      Thread theThread = new Thread(listener);
      theThread.start();

      // Read input from the keyboard and send it to everyone else.
      // The only way to quit is to hit control-c, but a quit command
      // could easily be added.

      System.out.print("Enter a username: ");
      Scanner keyboard = new Scanner(System.in);
      String username = keyboard.nextLine();
      serverOutput.writeBytes(username + "\n");
      // check if username is host
      if (username.equalsIgnoreCase("host")) {
        System.out.print("You are the host! Enter the Artist's name to begin the round: ");
        String artistName = keyboard.nextLine();  //add function that takes in String artistName to retrieve and play spotify song
        serverOutput.writeBytes(artistName + "\n");
      }
      else {  //if not host
        System.out.println("Welcome to the Guess the Artist Game! type 'join game' to opt-in to the game! if, not you may just lurk as a spectator.");
        String input = keyboard.nextLine();
        if (input.equals("join game")) {
          serverOutput.writeBytes("join game\n");
          System.out.println("You have opted-in to participate! Be the first person to answer the host's question correctly to earn points.");
        }
        else {
          serverOutput.writeBytes(input + "\n");
        }
      }

      while (true) {
        //commands for the host
        if (username.equalsIgnoreCase("host")) {
          System.out.println("Type 'points' to award points to a specific client");
          System.out.println("Type 'leaderboard' to display all clients and their scores");
          System.out.println("Or type 'artist' to start a new round");
          String command = keyboard.nextLine();
          serverOutput.writeBytes(command+"\n");
          if (command.equalsIgnoreCase("points")) { //award points to certain client by username
            System.out.print("Enter client's username: ");
            String u = keyboard.nextLine();
            System.out.print("Enter number of points to award to client: ");  //we should add a function to calculate points awarded based on obscurity of artist
            //pointsToAward(String hostAnswer); //either pull popularity stats from spotify or sentiment analysis from twitter and calculate
            int pts = keyboard.nextInt();
            serverOutput.writeBytes(u+"\n");
            serverOutput.writeByte(pts);
          }
          /*
          else {
            serverOutput.writeBytes(command + "\n");
          }
          */
        }
        else
        {
          //commands for clients
          String input = keyboard.nextLine();
          if (input.equals("join game")) {
            serverOutput.writeBytes("join game\n");
            System.out.println("You have opted-in to participate! Be the first person to answer the host's question correctly to earn points.");
          }
          else {
            serverOutput.writeBytes(input + "\n");
          }
        }
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
} // MtClient
