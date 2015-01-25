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

import pythagoras.f.IDimension;

import playn.core.Surface;
import playn.scene.Layer;

public class BoardView extends Layer {
  private static final float LINE_WIDTH = 2;
  private final Reversi game;

  public final float cellSize;

  public BoardView (Reversi game, IDimension viewSize) {
    this.game = game;
    float maxBoardSize = Math.min(viewSize.width(), viewSize.height()) - 20;
    this.cellSize = (float)Math.floor(maxBoardSize / game.boardSize);
  }

  /** Returns the offset to the center of cell {@code cc} (in x or y). */
  public float cell (int cc) {
    // cc*cellSize is upper left corner, then cellSize/2 to center,
    // then 1 to account for our 2 pixel line width
    return cc*cellSize + cellSize/2 + 1;
  }

  // we want two extra pixels in width/height to account for the grid lines
  @Override public float width () { return cellSize * game.boardSize + LINE_WIDTH; }
  @Override public float height () { return width(); } // width == height

  @Override protected void paintImpl (Surface surf) {
    surf.setFillColor(0xFF000000); // black with full alpha
    float top = 0, bot = height(), left = 0, right = width();

    // draw lines from top to bottom for each vertical grid line
    for (int yy = 0; yy <= game.boardSize; yy++) {
      float ypos = yy*cellSize+1;
      surf.drawLine(left, ypos, right, ypos, LINE_WIDTH);
    }

    // draw lines from left to right for each horizontal grid line
    for (int xx = 0; xx <= game.boardSize; xx++) {
      float xpos = xx*cellSize+1;
      surf.drawLine(xpos, top, xpos, bot, LINE_WIDTH);
    }
  }
}
