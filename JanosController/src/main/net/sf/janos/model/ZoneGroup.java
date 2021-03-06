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
package net.sf.janos.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An immutable data transfer object containing the members and controller of a
 * zone group.
 * 
 * @author David Wheeler
 * 
 */
public class ZoneGroup {

	private final List<String> members;
	private final String coordinator;
	private final String id;

	public ZoneGroup(String id, String coordinator, Collection<String> members) {
		this.members= new ArrayList<String>(members);
		if (!this.members.contains(coordinator)) {
			this.members.add(coordinator);
		}
		this.coordinator = coordinator;
		this.id = id;
	}

	public List<String> getMembers() {
		return members;
	}

	public String getCoordinator() {
		return coordinator;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof ZoneGroup) {
			ZoneGroup group = (ZoneGroup) obj;
			return group.getId().equals(getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
	  return id.hashCode();
	}
}
