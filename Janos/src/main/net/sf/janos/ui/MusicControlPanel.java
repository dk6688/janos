/*
 * Created on 01/11/2007
 * By David Wheeler
 * Student ID: 3691615
 */
package net.sf.janos.ui;

import java.io.IOException;

import net.sbbi.upnp.messages.UPNPResponseException;
import net.sf.janos.control.SonosController;
import net.sf.janos.model.TransportInfo.TransportState;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

/**
 * a UI component with actions for controlling the music eg. stop
 * 
 * @author David Wheeler
 * 
 */
public class MusicControlPanel extends Composite {

  private final SonosController controller;
  private Button play;

  public MusicControlPanel(Composite parent, int style, SonosController controller) {
    super(parent, style);
    this.controller = controller;
    buildComponents();
  }

  private void buildComponents() {
    RowLayout layout = new RowLayout(SWT.HORIZONTAL);
    layout.wrap = false;
    layout.spacing=4;
    setLayout(layout);
    
    final Scale volume = new Scale(this, SWT.HORIZONTAL);
    volume.setMinimum(0);
    volume.setMaximum(100);
    volume.setSelection(getVolume());
    Button previous = new Button(this, SWT.PUSH);
    previous.setText("<");
    previous.addMouseListener(new MouseAdapter() {
      public void mouseUp(MouseEvent e) {
        previous();
      }
    });
    // TODO initialize play and volume to correct values
    play = new Button(this, SWT.PUSH);
    play.setText(isPlaying() ? "Pause" : "Play");
    play.addMouseListener(new MouseAdapter() {
      public void mouseUp(MouseEvent e) {
        play();
      }
    });
    Button next = new Button(this, SWT.PUSH);
    next.setText(">");
    next.addMouseListener(new MouseAdapter() {
      public void mouseUp(MouseEvent e) {
        next();
      }
    });
    
    
    volume.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setVolume(volume.getSelection());
      }
    });
 }

  private boolean isPlaying() {
    try {
      return controller.getCurrentZonePlayer().getMediaRendererDevice().getAvTransportService().getTransportInfo().getState().equals(TransportState.PLAYING);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  protected void previous() {
    try {
      controller.getCurrentZonePlayer().getMediaRendererDevice().getAvTransportService().previous();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  protected void play() {
    try {
      if (play.getText().equals("Pause")) {
        play.setText("Play");
        controller.getCurrentZonePlayer().getMediaRendererDevice().getAvTransportService().pause();
      } else if (play.getText().equals("Play")) {
        play.setText("Pause");
        controller.getCurrentZonePlayer().getMediaRendererDevice().getAvTransportService().play();
      }
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  protected void next() {
    try {
      controller.getCurrentZonePlayer().getMediaRendererDevice().getAvTransportService().next();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  protected void setVolume(int vol) {
    try {
      controller.getCurrentZonePlayer().getMediaRendererDevice().getRenderingControlService().setVolume(vol);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  protected int getVolume() {
    try {
      return controller.getCurrentZonePlayer().getMediaRendererDevice().getRenderingControlService().getVolume();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UPNPResponseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return 0;
  }
}