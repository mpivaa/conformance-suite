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

package io.fintechlabs.testframework.condition;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import io.fintechlabs.testframework.logging.EventLog;
import io.fintechlabs.testframework.testmodule.Environment;

/**
 * Checks to make sure the "state" parameter matches the one that was saved previously.
 * 
 * @author jricher
 *
 */
public class CheckMatchingStateParameter extends AbstractCondition {

	/**
	 * @param testId
	 * @param log
	 */
	public CheckMatchingStateParameter(String testId, EventLog log, boolean optional) {
		super(testId, log, optional);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.Condition#evaluate(io.fintechlabs.testframework.testmodule.Environment)
	 */
	@Override
	@PreEnvironment(required = "callback_params", strings = "state")
	public Environment evaluate(Environment in) {
		if (!in.containsObj("callback_params")) {
			return error("Couldn't find callback parameters");
		}
		
		String expected = in.getString("state");
		String actual = in.getString("callback_params", "state");
		
		if (Strings.isNullOrEmpty(expected)) {
			// we didn't save a 'state' value, we need to make sure one wasn't returned
			if (Strings.isNullOrEmpty(actual)) {
				// we're good
				logSuccess("No state parameter to check");
				return in;
			} else {
				return error("State parameter did not match", args("expected", Strings.nullToEmpty(expected), "actual", Strings.nullToEmpty(actual)));
			}
		} else {
			// we did save a state parameter, make sure it's the same as before
			if (expected.equals(actual)) {
				// we're good
				logSuccess("Checking for state parameter", 
					args("state", Strings.nullToEmpty(actual)));
				
				return in;
			} else {
				return error("State parameter did not match");
			}
		}
		
	}

}
