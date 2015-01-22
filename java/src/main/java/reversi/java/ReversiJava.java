package reversi.java;

import playn.java.JavaPlatform;

import reversi.core.Reversi;

public class ReversiJava {

  public static void main (String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform plat = new JavaPlatform(config);
    new Reversi(plat);
    plat.start();
  }
}
