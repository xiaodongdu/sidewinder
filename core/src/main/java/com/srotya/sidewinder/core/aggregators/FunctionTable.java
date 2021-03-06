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
package com.srotya.sidewinder.core.aggregators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.reflections.Reflections;

/**
 * @author ambud
 */
@SuppressWarnings("unchecked")
public class FunctionTable {

	private static final Logger logger = Logger.getLogger(FunctionTable.class.getName());
	public static final String NONE = "none";
	public static final FunctionTable table = new FunctionTable();
	public Map<String, Class<? extends AggregationFunction>> functionMap;

	private FunctionTable() {
		functionMap = new HashMap<>();
	}

	static {
		String packageName = "com.srotya.sidewinder.core.aggregators";
		findAndRegisterFunctionsWithPackageName(packageName);
		FunctionTable.get().register(NONE, null);
	}

	public static void findAndRegisterFunctionsWithPackageName(String packageName) {
		Reflections reflections = new Reflections(packageName.trim());
		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(FunctionName.class);
		for (Class<?> annotatedClass : annotatedClasses) {
			FunctionName function = annotatedClass.getAnnotation(FunctionName.class);
			String[] aliases = function.alias();
			if (aliases == null || aliases.length == 0) {
				logger.warning("Ignoring aggregation function:" + annotatedClass.getName());
				continue;
			}
			for (String alias : aliases) {
				alias = alias.trim();
				if (!alias.isEmpty()) {
					FunctionTable.get().register(alias, (Class<? extends AggregationFunction>) annotatedClass);
					logger.fine("Registering function with alias:" + alias);
				}
			}
		}
	}

	public static FunctionTable get() {
		return table;
	}

	public void register(String name, Class<? extends AggregationFunction> functionClass) {
		functionMap.put(name.toLowerCase(), functionClass);
	}

	public Class<? extends AggregationFunction> lookupFunction(String name) {
		return functionMap.get(name.toLowerCase());
	}

	public Set<String> listFunctions() {
		return functionMap.keySet();
	}
}
