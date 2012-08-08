/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ccnt.hadoop;

import java.util.Iterator;
import java.util.List;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.ConsoleReader;

class ArgsCompletor implements Completer {
	private HadoopConsole hConsole;
	private ConsoleReader reader;

	public ArgsCompletor(HadoopConsole hConsole, ConsoleReader reader) {
		this.reader = reader;
		this.hConsole = hConsole;
	}

	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {
		// Guarantee that the final token is the one we're expanding

		if (buffer == null) {
			buffer = "";
		}

		buffer = buffer.substring(0, cursor);

		// gaoxiao
		// get pre Cmd
		// String[] buffers = reader.getCursorBuffer().toString().split("\\s");

		String preBuf = reader.getCursorBuffer().toString();
		String[] buffers = preBuf.split("\\s");
		ArgumentCompleter argumentCompleter = (ArgumentCompleter) reader
				.getCompleters().toArray()[0];

		int prevIndex = buffers.length - 2;
		if (buffer == "") {
			prevIndex = buffers.length - 1;
		}
		if (prevIndex < 0) {
			prevIndex = 0;
		}
		// System.out.println(prevIndex);
		String preCmd = buffers[prevIndex];
		String token = "";
		if (!buffer.endsWith(" ")) {
			String[] tokens = buffer.split(" ");

			if (tokens.length != 0) {
				token = tokens[tokens.length - 1];
			}
		}

		return completeCommand(buffer, token, candidates, preCmd);
	}

	private int completeCommand(String buffer, String token,
			List<String> candidates, String preCmd) {

		List<String> commands = HadoopConsole.commandMap.get(preCmd);

		if (commands != null) {
			for (String cmd : HadoopConsole.commandMap.get(preCmd)) {
				if (cmd.startsWith(token)) {
					candidates.add(cmd);
				}
			}
		} else {
			candidates.add("");
		}

		return buffer.lastIndexOf(" ") + 1;
	}
}
