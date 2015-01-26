package reversi.core;

import pythagoras.f.IDimension;
import playn.core.*;
import playn.scene.*;
import react.RMap;
import react.Value;

public class Reversi extends SceneGame {

  public static enum Piece { BLACK, WHITE }

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
    rootLayer.add(new GameView(this, size));

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
}
