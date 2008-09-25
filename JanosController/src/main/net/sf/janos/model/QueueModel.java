/*
   Copyright 2008 davidwheeler

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
package net.sf.janos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model for the queue list
 * @author David Wheeler
 *
 */
public class QueueModel {
  /**
   * The list of entries in the queue
   */
  private List<Entry> entries = new ArrayList<Entry>();
  
  /**
   * The index of the entry that is currently playing
   */
  private int nowPlaying = -1;

  /**
   * The list of listeners
   */
  private List<QueueModelListener> listeners = new ArrayList<QueueModelListener>();;
  
  /**
   * @param index the index of the entry to get the title from
   * @return a String of the title of the entry at index
   */
  public String getTitle(int index) {
    return entries.get(index).getTitle();
  }
  
  /**
   * Sets the index of the entry now playing
   * @param i the new index
   */
  public void setNowPlaying(int i) {
    nowPlaying = i;
    fireNowPlayingChanged();
  }

  /**
   * @param index
   * @return true if the given index matches the index marked as now playing
   */
  public boolean isNowPlaying(int index) {
    return index == nowPlaying;
  }
  
  /**
   * Replaces the list of entries with the one provided
   * @param entries
   */
  public void setEntries(List<Entry> entries) {
    this.entries = entries;
    if (nowPlaying >= this.entries.size() -1) {
      setNowPlaying(-1);
    }
    fireEntriesChanged();
  }

  /**
   * @return the number of entries in the queue
   */
  public int getSize() {
    return entries.size();
  }

  /**
   * @return the index of the entry that is currently playing
   */
  public int getNowPlaying() {
    return nowPlaying;
  }
  
  /**
   * Adds a listener to be notified when the list of entries changes or the now
   * playing entry changes
   * 
   * @param listener
   */
  public void addQueueModelListener(QueueModelListener listener) {
    this.listeners.add(listener);
  }
  
  /**
   * Removes the listener from the notification list
   * @param listener
   */
  public void removeQueueModelListener(QueueModelListener listener) {
    this.listeners.remove(listener);
  }

  /**
   * Notifies all listeners that the value of nowPlaying has changed
   *
   */
  private void fireNowPlayingChanged() {
    for (QueueModelListener listener : listeners) {
      listener.nowPlayingChanged(this);
    }
  }

  /**
   * Notifies all listeners that the value of nowPlaying has changed
   *
   */
  private void fireEntriesChanged() {
    for (QueueModelListener listener : listeners) {
      listener.entriesChanged(this);
    }
  }
}