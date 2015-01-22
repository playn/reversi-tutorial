package reversi.android;

import playn.android.GameActivity;

import reversi.core.Reversi;

public class ReversiActivity extends GameActivity {

  @Override public void main () {
    new Reversi(platform());
  }
}
