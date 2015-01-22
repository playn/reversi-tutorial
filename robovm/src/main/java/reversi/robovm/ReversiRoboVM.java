package reversi.robovm;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;

import playn.robovm.RoboPlatform;
import reversi.core.Reversi;

public class ReversiRoboVM extends UIApplicationDelegateAdapter {

  @Override
  public boolean didFinishLaunching (UIApplication app, UIApplicationLaunchOptions launchOpts) {
    RoboPlatform.Config config = new RoboPlatform.Config();
    // use config to customize the RoboVM platform, if needed
    RoboPlatform pf = RoboPlatform.register(app, config);
    addStrongRef(pf);

    pf.run(new Reversi());
    return true;
  }

  public static void main (String[] args) {
    NSAutoreleasePool pool = new NSAutoreleasePool();
    UIApplication.main(args, null, ReversiRoboVM.class);
    pool.close();
  }
}
