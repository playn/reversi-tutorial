package reversi.core;

import java.util.List;

import pythagoras.f.IDimension;
import react.RMap;
import react.Slot;
import react.Value;

import playn.core.*;
import playn.scene.*;
import playn.scene.Mouse;
import playn.scene.Pointer;

public class Reversi extends SceneGame {

  public static enum Piece {
    BLACK, WHITE;
    public Piece next () { return values()[(ordinal()+1) % values().length]; }
  }

  public static class Coord {
    public final int x, y;

    public Coord (int x, int y) {
      assert x >= 0 && y >= 0;
      this.x = x;
      this.y = y;
    }

    public boolean equals (Coord other) {
      return other.x == x && other.y == y;
    }
    @Override public boolean equals (Object other) {
      return (other instanceof Coord) && equals((Coord)other);
    }
    @Override public int hashCode () { return x ^ y; }
    @Override public String toString () { return "+" + x + "+" + y; }
  }

  public final int boardSize = 8;
  public final RMap<Coord,Piece> pieces = RMap.create();
  public final Value<Piece> turn = Value.create(null);
  public final Logic logic = new Logic(boardSize);

  public Reversi (Platform plat) {
    super(plat, 33); // update our "simulation" 33ms (30 times per second)

    // figure out how big the game view is
    final IDimension size = plat.graphics().viewSize;

    // create a layer that just draws a grey background
    rootLayer.add(new Layer() {
      protected void paintImpl (Surface surf) {
        surf.setFillColor(0xFFCCCCCC).fillRect(0, 0, size.width(), size.height());
      }
    });

    // create and add a game view
    final GameView gview = new GameView(this, size);
    rootLayer.add(gview);

    // wire up a turn handler
    turn.connect(new Slot<Piece>() {
      private boolean lastPlayerPassed = false;
      @Override public void onEmit (Piece color) {
        List<Coord> plays = logic.legalPlays(pieces, color);
        if (!plays.isEmpty()) {
          lastPlayerPassed = false;
          gview.showPlays(plays, color);
        } else if (lastPlayerPassed) {
          endGame();
        } else {
          lastPlayerPassed = true;
          turn.update(color.next());
        }
      }
    });

    // start the game
    reset();
  }

  /** Clears the board and sets the 2x2 set of starting pieces in the middle. */
  private void reset () {
    pieces.clear();
    int half = boardSize/2;
    pieces.put(new Coord(half-1, half-1), Piece.WHITE);
    pieces.put(new Coord(half  , half-1), Piece.BLACK);
    pieces.put(new Coord(half-1, half  ), Piece.BLACK);
    pieces.put(new Coord(half  , half  ), Piece.WHITE);
    turn.updateForce(Piece.BLACK);
  }

  private void endGame () {
    // TODO
  }
}
