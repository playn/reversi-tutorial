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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import playn.core.*;
import playn.scene.*;
import pythagoras.f.IDimension;
import react.RMap;

import reversi.core.Reversi.Piece;
import reversi.core.Reversi.Coord;

public class GameView extends GroupLayer {
  private final Reversi game;
  private final BoardView bview;
  private final GroupLayer pgroup = new GroupLayer();

  private final Tile[] ptiles = new Tile[Piece.values().length];
  private final Map<Coord,ImageLayer> pviews = new HashMap<>();

  public GameView (Reversi game, IDimension viewSize) {
    this.game = game;
    this.bview = new BoardView(game, viewSize);
    addCenterAt(bview, viewSize.width()/2, viewSize.height()/2);
    addAt(pgroup, bview.tx(), bview.ty());

    // draw a black piece and white piece into a single canvas image
    float size = bview.cellSize-2, hsize = size/2;
    Canvas canvas = game.plat.graphics().createCanvas(2*size, size);
    canvas.setFillColor(0xFF000000).fillCircle(hsize, hsize, hsize).
      setStrokeColor(0xFFFFFFFF).setStrokeWidth(2).strokeCircle(hsize, hsize, hsize-1);
    canvas.setFillColor(0xFFFFFFFF).fillCircle(size+hsize, hsize, hsize).
      setStrokeColor(0xFF000000).setStrokeWidth(2).strokeCircle(size+hsize, hsize, hsize-1);

    // convert the image to a texture and extract a texture region (tile) for each piece
    Texture ptex = canvas.toTexture(Texture.Config.UNMANAGED);
    ptiles[Piece.BLACK.ordinal()] = ptex.tile(0, 0, size, size);
    ptiles[Piece.WHITE.ordinal()] = ptex.tile(size, 0, size, size);

    // dispose our pieces texture when this layer is disposed
    onDisposed(ptex.disposeSlot());

    game.pieces.connect(new RMap.Listener<Coord,Piece>() {
      @Override public void onPut (Coord coord, Piece piece) { setPiece(coord, piece); }
      @Override public void onRemove (Coord coord) { clearPiece(coord); }
    });
  }

  public void showPlays (List<Coord> coords, Piece color) {
    final List<ImageLayer> plays = new ArrayList<>();
    for (Coord coord : coords) {
      ImageLayer pview = addPiece(coord, color);
      pview.setAlpha(0.3f);
      // TODO: listen for a click on pview and make that move
      plays.add(pview);
    }
  }

  private ImageLayer addPiece (Coord at, Piece piece) {
    ImageLayer pview = new ImageLayer(ptiles[piece.ordinal()]);
    pview.setOrigin(Layer.Origin.CENTER);
    pgroup.addAt(pview, bview.cell(at.x), bview.cell(at.y));
    return pview;
  }

  private void setPiece (Coord at, Piece piece) {
    ImageLayer pview = pviews.get(at);
    if (pview == null) {
      pviews.put(at, addPiece(at, piece));
    } else {
      pview.setTile(ptiles[piece.ordinal()]);
    }
  }

  private void clearPiece (Coord at) {
    ImageLayer pview = pviews.remove(at);
    if (pview != null) pview.close();
  }
}
