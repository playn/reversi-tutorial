package reversi.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import reversi.core.Reversi;

public class ReversiActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new Reversi());
  }
}
