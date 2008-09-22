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
package net.sf.janos.ui.zonelist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sbbi.upnp.devices.DeviceIcon;
import net.sf.janos.control.SonosController;
import net.sf.janos.control.ZoneListSelectionListener;
import net.sf.janos.control.ZonePlayer;
import net.sf.janos.model.ZonePlayerModel;
import net.sf.janos.model.ZonePlayerModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ZoneList extends Composite implements ZonePlayerModelListener {
  
  private static final Log LOG = LogFactory.getLog(ZoneList.class);
  
  private final List<ZoneListSelectionListener> selectionListeners = new ArrayList<ZoneListSelectionListener>();
  private final Table zoneTable;
  private final ZonePlayerModel model;
  private int currentSelection = -1;

  public ZoneList(Composite parent, int style, SonosController controller) {
    super(parent, style);
    this.model = controller.getZonePlayerModel();

    setLayout(new FillLayout(SWT.VERTICAL));
    zoneTable = new Table(this, SWT.SINGLE | SWT.VIRTUAL);
    TableColumn zoneColumn = new TableColumn(zoneTable, SWT.NONE);
    zoneColumn.setText("Zones");
    zoneColumn.setWidth(180);
//    zoneTable.setToolTipText("Drag a zone onto another to link them");
    zoneTable.addListener(SWT.SetData, new Listener() {
      public void handleEvent(Event event) {
        TableItem item = (TableItem)event.item;
        int index = event.index;
        item.setText(model.get(index).getDevicePropertiesService().getZoneAttributes().getName());
        setIcon(item, model.get(index));
      }
    });
    
    zoneTable.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent arg0) {
        // Don't care
      }
      public void widgetSelected(SelectionEvent arg0) {
        int newSel = zoneTable.getSelectionIndex();
        if (newSel != currentSelection) {
          currentSelection = newSel;
          fireZoneSelectionChanged(model.get(newSel));
        }
      }
    });
    currentSelection = zoneTable.getSelectionIndex();
    
    model.addZonePlayerModelListener(this);
    for (ZonePlayer zp : model.getAllZones()) {
      addZonePlayerToDisplay(zp);
    }
  }
  
  @Override
  public void dispose() {
    super.dispose();
//    for (TableColumn col : zoneTable.getColumns()) {
//      col.dispose();
//    }
//    zoneTable.dispose();
  }
  
  public void zonePlayerAdded(final ZonePlayer dev, ZonePlayerModel model) {
    addZonePlayerToDisplay(dev);
  }
  
  private void addZonePlayerToDisplay(final ZonePlayer dev) {
    getDisplay().asyncExec(new Runnable() {
      public void run() {
        zoneTable.setItemCount(model.getSize());
        if (zoneTable.getSelectionIndex() == -1) {
          // BUG for some reason, this call doesn't fire selection events. so we have to do it ourselfs!
          zoneTable.select(0);
          currentSelection=0;
          fireZoneSelectionChanged(model.get(currentSelection));
        }
        redraw();
      }

    });
  }
  
  private void setIcon(final TableItem newItem, final ZonePlayer dev) {
    // TODO use image cache
    Image oldImage = newItem.getImage();
    if (oldImage != null) {
      oldImage.dispose();
    }
    List<DeviceIcon> icons = dev.getMediaRendererDevice().getUPNPDevice().getDeviceIcons();
    if (icons == null || icons.isEmpty()) {
      newItem.setImage((Image)null);
      return;
    }
    final DeviceIcon deviceIcon = icons.get(0);
    SonosController.getInstance().getExecutor().execute(new Runnable() {
      public void run() {
        InputStream is = null;
        URL url = deviceIcon.getUrl();
        try {
          is = url.openStream();
          final ImageData[] images = new ImageLoader().load(is);
          if (images != null && images.length > 0) {
            newItem.getDisplay().asyncExec(new Runnable() {
              public void run() {
                if (!newItem.isDisposed()) {
                  newItem.setImage(new Image(newItem.getDisplay(), images[0]));
                }
              }
            });
          }
        } catch (IOException e) {
          LOG.error("Couldn't load image " + url, e);
        } finally {
          if (is != null) {
            try {
              is.close();
            } catch (IOException ex1) {}
          }
        }
      }
    });
  }

  
  public void zonePlayerRemoved(final ZonePlayer dev, final ZonePlayerModel model) {
    getDisplay().asyncExec(new Runnable() {
      public void run() {
//       zoneTable.remove(dev.getDevicePropertiesService().getZoneAttributes().getName());
        zoneTable.setItemCount(model.getSize());
        redraw();
      }
    });
  }
  
  public ZonePlayer getSelectedZone() {
    return model.get(currentSelection);
  }

  public void addSelectionListener(ZoneListSelectionListener l) {
    this.selectionListeners.add(l);
    if (currentSelection >= 0) {
      l.zoneSelectionChangedTo(model.get(currentSelection));
    }
  }
  
  public void removeSelectionListener(ZoneListSelectionListener l) {
    this.selectionListeners.remove(l);
  }
  
  protected void fireZoneSelectionChanged(ZonePlayer newSelection) {
    for (ZoneListSelectionListener l : this.selectionListeners) {
      l.zoneSelectionChangedTo(newSelection);
    }
  }
  
}
