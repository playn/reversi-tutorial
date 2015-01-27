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

import pythagoras.f.FloatMath;
import pythagoras.f.IDimension;
import pythagoras.f.Point;
import react.RMap;

import playn.core.*;
import playn.scene.*;
import playn.scene.Mouse;
import playn.scene.Pointer;

import reversi.core.Reversi.Piece;
import reversi.core.Reversi.Coord;

import tripleplay.anim.Animation;

public class GameView extends GroupLayer {
  private final Reversi game;
  private final BoardView bview;
  private final GroupLayer pgroup = new GroupLayer();

  private final Tile[] ptiles = new Tile[Piece.values().length];
  private final Map<Coord,ImageLayer> pviews = new HashMap<>();

  private final Sound click;
  private final FlipBatch flip;

  public GameView (Reversi game, IDimension viewSize) {
    this.game = game;
    this.bview = new BoardView(game, viewSize);
    this.click = game.plat.assets().getSound("sounds/click");
    this.flip = new FlipBatch(game.plat.graphics().gl, 2);

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

  @Override public void close () {
    super.close();
    flip.close();
  }

  public void showPlays (List<Coord> coords, final Piece color) {
    final List<ImageLayer> plays = new ArrayList<>();
    for (final Coord coord : coords) {
      ImageLayer pview = addPiece(coord, color);
      // fade the piece in
      pview.setVisible(false).setAlpha(0);
      game.anim.setVisible(pview, true).then().tweenAlpha(pview).to(0.3f).in(300);
      // when the player clicks on a potential play, commit that play as their move
      pview.events().connect(new Pointer.Listener() {
        @Override public void onStart (Pointer.Interaction iact) {
          // clear out the potential plays layers
          for (ImageLayer play : plays) play.close();
          // apply this play to the game state
          game.logic.applyPlay(game.pieces, color, coord);
          // and move to the next player's turn
          game.turn.update(color.next());
        }
      });
      // when the player hovers over a potential play, highlight it
      pview.events().connect(new Mouse.Listener() {
        @Override public void onHover (Mouse.HoverEvent event, Mouse.Interaction iact) {
          iact.hitLayer.setAlpha(event.inside ? 0.6f : 0.3f);
        }
      });
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
      pviews.put(at, pview = addPiece(at, piece));
      // animate the piece view "falling" into place
      pview.setVisible(false).setScale(2);
      game.anim.setVisible(pview, true).then().
        tweenScale(pview).to(1).in(500).bounceOut();
      game.anim.delay(250).then().play(click);
      game.anim.addBarrier();

    } else {
      final ImageLayer fview = pview;
      final Tile tile = ptiles[piece.ordinal()];
      final Point eye = LayerUtil.layerToScreen(pview, fview.width()/2, fview.height()/2);
      Animation.Value flipAngle = new Animation.Value() {
        public float initial () { return flip.angle; }
        public void set (float value) { flip.angle = value; }
      };
      game.anim.
        action(new Runnable() { public void run () {
          flip.eyeX = eye.x;
          flip.eyeY = eye.y;
          fview.setBatch(flip);
        }}).
        then().tween(flipAngle).from(0).to(FloatMath.PI/2).in(150).
        then().action(new Runnable() { public void run () {
          fview.setTile(tile);
        }}).
        then().tween(flipAngle).to(FloatMath.PI).in(150).
        then().action(new Runnable() { public void run () {
          fview.setBatch(null);
        }});
      game.anim.addBarrier();
    }
  }

  private void clearPiece (Coord at) {
    ImageLayer pview = pviews.remove(at);
    if (pview != null) pview.close();
  }
}
