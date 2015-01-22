package reversi.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import reversi.core.Reversi;

public class ReversiJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new Reversi());
  }
}
