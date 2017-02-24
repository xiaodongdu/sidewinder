/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.sidewinder.core.aggregators.windowed;

import java.util.List;

import com.srotya.sidewinder.core.aggregators.FunctionTable;
import com.srotya.sidewinder.core.storage.DataPoint;

/**
 * @author ambud
 */
public class WindowedMean extends ReducingWindowedAggregator {

	@Override
	public void init(Object[] args) throws Exception {
		if (args.length > 1) {
			args[1] = FunctionTable.SMEAN;
		} else {
			args = new Object[] { args[0], FunctionTable.SMEAN };
		}
		super.init(args);
	}

	@Override
	public List<DataPoint> aggregateAfterReduction(List<DataPoint> datapoints) {
		return datapoints;
	}

}