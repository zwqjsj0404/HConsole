package org.ccnt.hadoop;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;

public class CommandRunner {

	private static FsShell shell = HadoopConsole.getShell();

	public void run(String[] cmds) {
		runCmds(cmds);
	}

	private void runCmds(String[] cmds) {

		String topCmd = cmds[0];
		String[] args = Arrays.copyOfRange(cmds, 1, cmds.length);

		if (StringUtils.equals(topCmd, "fs")) {
			handleFsShell(args);
		} else if (StringUtils.equals(topCmd, "dfsadmin")) {
			handleDfsAdmin(args);
		}
	}

	private void handleDfsAdmin(String[] args) {
		try {
			ToolRunner.run(new DFSAdmin(), args);
		} catch (Exception e) {
			// TODO 当前不进行处理
			e.printStackTrace();
		}
	}

	private void handleFsShell(String[] args) {
		try {
			ToolRunner.run(shell, args);
		} catch (Exception e) {
			// TODO 当前不进行处理
			e.printStackTrace();
		}
	}
}
