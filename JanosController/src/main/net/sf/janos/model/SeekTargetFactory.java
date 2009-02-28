/*
 * Copyright 2008 David Wheeler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.janos.model;

import net.sf.janos.util.TimeUtilities;

/**
 * Allows the creation of SeekTargets.
 * @author David Wheeler
 *
 */
public class SeekTargetFactory {

  /**
   * 
   * @param track zero-relative track number to seek to
   * @return a SeekTarget to seek to the given track number
   */
  public static SeekTarget createTrackSeekTarget(int track) {
    return new SeekTarget(SeekMode.TRACK_NR, Integer.toString(track+1));
  }
  
  /**
   * 
   * @param absTime the time to seek to, in millis.
   * @return a SeekTarget to seek to the given absolute time
   */
  public static SeekTarget createAbsTimeSeekTarget(long absTime) {
    return new SeekTarget(SeekMode.ABS_TIME, TimeUtilities.convertLongToDuration(absTime));
  }
  
  /**
   * 
   * @param relTime the time to seek to, in millis.
   * @return a SeekTarget to seek to the given relative time
   */
  public static SeekTarget createRelTimeSeekTarget(long relTime) {
    return new SeekTarget(SeekMode.REL_TIME, TimeUtilities.convertLongToDuration(relTime));
  }
  
  /**
   * 
   * @param target zero-relative absolute index to seek to
   * @return
   */
  public static SeekTarget createAbsCountSeekTarget(int target) {
    return new SeekTarget(SeekMode.ABS_COUNT, Integer.toString(target+1));
  }

  /**
   * 
   * @param target zero-relative context relative index to seek to
   * @return
   */
  public static SeekTarget createRelCountSeekTarget(int target) {
    return new SeekTarget(SeekMode.REL_COUNT, Integer.toString(target+1));
  }
  
  // TODO the other SeekModes...
}
