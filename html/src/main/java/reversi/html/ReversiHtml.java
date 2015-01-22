package reversi.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import reversi.core.Reversi;

public class ReversiHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("reversi/");
    new Reversi(plat);
    plat.start();
  }
}
