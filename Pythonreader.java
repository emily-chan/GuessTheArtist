//https://stackoverflow.com/questions/27267391/running-a-py-file-from-java/27267509

import java.io.*;
import java.util.*;
import java.lang.*;

public class Pythonreader {
  public static int runAnalysis(String artistName) {
    try {
        String term = "python twitterexample.py " + artistName;
        Process p = Runtime.getRuntime().exec(term);
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String ret1 = in.readLine();
        double ret = Double.parseDouble(ret1);
        int ret3 = (int)ret;
        p.destroy();
        return ret3;
    } catch (IOException E) {E.printStackTrace();}
      return -1;
  }

}
