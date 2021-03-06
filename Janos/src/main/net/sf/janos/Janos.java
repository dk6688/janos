/*
   Copyright 2007 David Wheeler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package net.sf.janos;

import java.util.Timer;
import java.util.TimerTask;

import net.sf.janos.control.SonosController;
import net.sf.janos.ui.SonosControllerShell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;


public class Janos implements Runnable {
  
  private static final Log LOG = LogFactory.getLog(Janos.class);

  /**
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) {
    if (args.length > 1) {
      System.out.println("Usage: Janos [port]");
      System.exit(1);
    }
    
    if (args.length == 1) {
      System.setProperty("net.sbbi.upnp.Discovery.bindPort", args[0]);
    }
     
    /*
     * [DW] For some reason unknown to me, given: 
     * 1) arch is intel
     * 2) os is Mac OS X 
     * 3) running in eclipse
     * 4) using SWT
     * no exceptions are displayed in the eclipse console in the main thread. 
     * To work around this, we just do everything in a new thread :-)
     */
    if (Boolean.getBoolean("net.sf.janos.forkNewThread"))
    {
      try {
        Thread mainThread = new Thread(new Janos(), "Janos-SWT");
        mainThread.start();
        mainThread.join();
      } catch (Throwable t) {
        LogFactory.getLog(Janos.class).fatal("Could not start thread: ", t);
        System.exit(1);
      }
    } else {
      new Janos().run();
    }
  }

  public void run() {
    Display.setAppName("Janos");
    final SonosController controller = SonosController.getInstance();
    try {
      SonosControllerShell shell = new SonosControllerShell(new Display(), controller);
      try {
        Timer zonePollerTimer = new Timer("ZonePoller", true);
        TimerTask zonePollerTask = new TimerTask() {

          @Override
          public void run() {
            controller.searchForDevices();
            long pollPeriod = Long.parseLong(System.getProperty("net.sf.janos.pollPeriod", "5000"));
            controller.purgeStaleDevices(pollPeriod*2);
          }
        };
        long pollPeriod = Long.parseLong(System.getProperty("net.sf.janos.pollPeriod", "5000"));
        zonePollerTimer.scheduleAtFixedRate(zonePollerTask, 0, pollPeriod);
        Thread.sleep(Integer.parseInt(System.getProperty("net.sf.janos.searchTime", "1000")));
      } catch (NumberFormatException e) {
        LogFactory.getLog(Janos.class).warn("Sleep interrupted:", e);
      } catch (InterruptedException e) {
        LogFactory.getLog(Janos.class).warn("Sleep interrupted:", e);
      }
      ApplicationContext.create(controller, shell);
      shell.start();
    } catch (Throwable t) {
      LOG.fatal("Error running Janos", t);
    } finally {
      // attempt to unregister from zone players
      controller.dispose();
    }
    
  }

}
