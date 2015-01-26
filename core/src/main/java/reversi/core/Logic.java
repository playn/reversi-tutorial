/**
 * Copyright 2010-2015 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reversi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import reversi.core.Reversi.Coord;
import reversi.core.Reversi.Piece;

/** "Does the math" for Reversi. */
public class Logic {
  public final int boardSize;

  public Logic (int boardSize) {
    this.boardSize = boardSize;
  }

  /** Returns true if the specified player can play a piece at the specified coordinate. */
  public boolean isLegalPlay (Map<Coord,Piece> board, Piece color, Coord coord) {
    if (!inBounds(coord.x, coord.y) || board.containsKey(coord)) return false;

    // look in each direction from this piece; if we see the other piece color and then one of our
    // own, then this is a legal move
    for (int ii = 0; ii < DX.length; ii++) {
      boolean sawOther = false;
      int x = coord.x, y = coord.y;
      for (int dd = 0; dd < boardSize; dd++) {
        x += DX[ii];
        y += DY[ii];
        if (!inBounds(x, y)) break; // stop when we end up off the board
        Piece piece = board.get(new Coord(x, y));
        if (piece == null) break;
        else if (piece != color) sawOther = true;
        else if (sawOther) return true;
        else break;
      }
    }

    return false;
  }

  /** Applies the specified play (caller must have already checked its legality).
    * Flips pieces as appropriate. */
  public void applyPlay (Map<Coord,Piece> board, Piece color, Coord coord) {
    List<Coord> toFlip = new ArrayList<>();
    // place this piece into the game state
    board.put(coord, color);
    // determine where this piece captures other pieces
    for (int ii = 0; ii < DX.length; ii++) {
      // look in this direction for captured pieces
      int x = coord.x, y = coord.y;
      for (int dd = 0; dd < boardSize; dd++) {
        x += DX[ii];
        y += DY[ii];
        if (!inBounds(x, y)) break; // stop when we end up off the board
        Coord fc = new Coord(x, y);
        Piece piece = board.get(fc);
        if (piece == null) break;
        else if (piece != color) toFlip.add(fc);
        else { // piece == color
          for (Coord tf : toFlip) board.put(tf, color); // flip it!
          break;
        }
      }
      toFlip.clear();
    }
  }

  /** Returns all legal plays for the player with the specified color. */
  public List<Coord> legalPlays (Map<Coord,Piece> board, Piece color) {
    List<Coord> plays = new ArrayList<>();
    // search every board position for a legal move; the force, it's so brute!
    for (int yy = 0; yy < boardSize; yy++) {
      for (int xx = 0; xx < boardSize; xx++) {
        Coord coord = new Coord(xx, yy);
        if (board.containsKey(coord)) continue;
        if (isLegalPlay(board, color, coord)) plays.add(coord);
      }
    }
    return plays;
  }

  private final boolean inBounds (int x, int y) {
    return (x >= 0) && (x < boardSize) && (y >= 0) && (y < boardSize);
  }

  protected static final int[] DX = { -1,  0,  1, -1, 1, -1, 0, 1 };
  protected static final int[] DY = { -1, -1, -1,  0, 0,  1, 1, 1 };
}
