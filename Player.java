import java.io.*;

import java.util.*;

public class Player implements Comparable<Player> {
  public String username;
  public int score;
  public ArrayList<Player> players = new ArrayList<Player>();
  
  public Player(String s) {
    this.username = s;
    this.score = 0;
  }

  public String toString() {
    String info = this.username + ": " + this.score + " points";
    return info;
  }
  public void addPoints(int p) {
    this.score = this.score + p;
  }
  
  public void setScore(int p){
    this.score = p;
  }

  public void setUsername(String user) {
    this.username = user;
  }

  public String getUsername() {
    return this.username;
  }

  public int getScore() {
    return this.score;
  }

  public int compareTo(Player p) {
    int compareScore = ((Player) p).getScore();
    return this.score - compareScore;
  }
}