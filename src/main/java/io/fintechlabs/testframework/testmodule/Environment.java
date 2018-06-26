/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.testmodule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * An element for storing the current running state of a test module in a way that it can be passed around.
 *
 * This object stores JSON indexed by strings. Furthermore, the JSON can be indexed by foo.bar.baz selectors.
 *
 * @author jricher
 *
 */
public class Environment {

	/**
	 * Set up a lock for threading purposes
	 */
	private ReentrantLock lock = new ReentrantLock(true); // set with fairness policy to get up control to the longest waiting thread

	private static final String STRING_VALUES = "_STRING_VALUES";
	private Map<String, JsonObject> store = Maps.newHashMap(
		ImmutableMap.of(STRING_VALUES, new JsonObject())); // make sure we start with a place to put the string values
	
	private Map<String, String> keyMap = new HashMap<>();

	/**
	 * Look to see if the JSON object is in this environment
	 *
	 * @param objId
	 * @return
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsObj(String objId) {
		return store.containsKey(getEffectiveKey(objId));
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public JsonObject get(String key) {
		return store.get(getEffectiveKey(key));
	}

	/**
	 * Remove a JSON object from this environment
	 *
	 * @param key
	 */
	public void remove(String key) {
		if (isKeyMapped(key)) {
			// if it's a mapping just remove the mapping
			unmapKey(key);
		} else {
			// otherwise remove it directly
			store.remove(key);
		}
	}

	/**
	 * Look up a single-string entry
	 *
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(STRING_VALUES, key);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public JsonObject put(String key, JsonObject value) {
		return store.put(getEffectiveKey(key), value);
	}

	/**
	 * Store a single string as a value
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public JsonObject putString(String key, String value) {
		JsonObject o = get(STRING_VALUES);
		o.addProperty(key, value);
		return o;
	}

	/**
	 * Search the environment for a key with lookup values separated by ".", so "foo.bar.baz" is found in:
	 *
	 * {
	 *  foo: {
	 *    bar: {
	 *      baz: "value"
	 *    }
	 *  }
	 * }
	 *
	 * @param path
	 */
	public String getString(String objId, String path) {
		JsonElement e = findElement(objId, path);

		if (e != null) {
			if (e.isJsonPrimitive()) {
				return e.getAsString();
			} else {
				// it wasn't a primitive
				return null;
			}
		} else {
			// we didn't find it
			return null;
		}
	}

	public Integer getInteger(String objId, String path) {
		JsonElement e = findElement(objId, path);
		if (e != null) {
			if (e.isJsonPrimitive()) {
				return e.getAsInt();
			} else {
				// it wasn't a primitive
				return null;
			}
		} else {
			return null;
		}
	}

	public Long getLong(String objId, String path) {
		JsonElement e = findElement(objId, path);
		if (e != null) {
			if (e.isJsonPrimitive()) {
				return e.getAsLong();
			} else {
				// it wasn't a primitive
				return null;
			}
		} else {
			return null;
		}
	}

	public JsonElement findElement(String objId, String path) {

		//
		// TODO: memoize this lookup for efficiency
		//

		// start at the top
		JsonElement e = get(objId);

		if (e == null) {
			return null;
		}

		Iterable<String> parts = Splitter.on('.').split(path);
		Iterator<String> it = parts.iterator();

		while (it.hasNext()) {
			String p = it.next();
			if (e.isJsonObject()) {
				JsonObject o = e.getAsJsonObject();
				if (o.has(p)) {
					e = o.get(p); // found the next level
					if (!it.hasNext()) {
						// we've reached a leaf at the right part of the key, return what we found
						return e;
					}
				} else {
					// didn't find it, stop processing
					break;
				}
			} else {
				// didn't find it, stop processing
				break;
			}
		}

		// didn't find it
		return null;

	}

	/**
	 * @return
	 */
	public Set<String> allObjectIds() {
		return store.keySet();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Environment [store=" + store + ", keyMap=" + keyMap + "]";
	}

	/**
	 * @return the lock
	 */
	public ReentrantLock getLock() {
		return lock;
	}
	
	/**
	 * If the key is mapped to another value, get the underlying value. Otherwise return the input key.
	 * @param key
	 * @return
	 */
	public String getEffectiveKey(String key) {
		if (keyMap.containsKey(key)) {
			return keyMap.get(key);
		} else {
			return key;
		}
	}
	
	/**
	 * Add a mapping from one key value to another. When things are looked up by "from" it will look for "to" in the storage.
	 * 
	 * This lookup does not chain to multiple levels -- if "to" is a mapping to something else, it will not be found.
	 * 
	 * @param from
	 * @param to
	 * @return the previously mapped "to" or "null" if not mapped
	 */
	public String mapKey(String from, String to) {
		return keyMap.put(from, to);
	}
	
	/**
	 * Remove a mapped key
	 * 
	 * @param key
	 * @return the previously mapped key or "null" if not mapped
	 */
	public String unmapKey(String key) {
		return keyMap.remove(key);
	}
	
	/**
	 * Test if a given key is a mapping to another value
	 * @param key
	 * @return
	 */
	public boolean isKeyMapped(String key) {
		return keyMap.containsKey(key);
	}

}
