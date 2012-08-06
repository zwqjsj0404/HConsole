package org.ccnt.hadoop;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.History;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.util.ToolRunner;

import com.google.common.collect.Lists;

/**
 * �ṩhadoop���������Զ���ȫ����
 * 
 * @author gaoxiao
 * 
 */
public class HadoopConsole {

	public static final Map<String, ArrayList<String>> commandMap = new HashMap<String, ArrayList<String>>();

	static final String HISTORYFILE = ".hadoop_history";
	static String historyFile = System.getProperty("user.home")
			+ File.separator + HISTORYFILE;

	static {
		commandMap.put("fs", Lists.newArrayList("-fs", "-conf", "-D", "-jt",
				"-files", "-libjars", "-archives", "-ls", "-lsr", "-du",
				"-dus", "-count", "-mv", "-mkdir", "-cp", "-rm", "-rmr",
				"-expunge", "-put", "-copyFromLocal", "-moveFromLocal", "-get",
				"-getmerge", "-cat", "-text", "-copyToLocal", "-chmod",
				"-chown", "-chgrp", "-help"));
		commandMap.put("namenode", Lists.newArrayList("-c", "-d"));
		commandMap.put("secondarynamenode", Lists.newArrayList("-c", "-d"));
		commandMap.put("datanode", Lists.newArrayList("-c", "-d"));
		commandMap.put("dfsadmin", Lists.newArrayList("-c", "-d"));
		commandMap.put("mradmin", Lists.newArrayList("-c", "-d"));
		commandMap.put("balancer", Lists.newArrayList("-c", "-d"));
		commandMap.put("fetchdt", Lists.newArrayList("-c", "-d"));
		commandMap.put("jobtracker", Lists.newArrayList("-c", "-d"));
		commandMap.put("pipes", Lists.newArrayList("-c", "-d"));
		commandMap.put("tasktracker", Lists.newArrayList("-c", "-d"));
		commandMap.put("historyserver", Lists.newArrayList("-c", "-d"));
		commandMap.put("job", Lists.newArrayList("-c", "-d"));
		commandMap.put("queue", Lists.newArrayList("-c", "-d"));
		commandMap.put("version", Lists.newArrayList("-c", "-d"));
		commandMap.put("jar", Lists.newArrayList("-c", "-d"));
		commandMap.put("distcp", Lists.newArrayList("-c", "-d"));
		commandMap.put("archive", Lists.newArrayList("-c", "-d"));
		commandMap.put("classpath", Lists.newArrayList("-c", "-d"));
		commandMap.put("daemonlog", Lists.newArrayList("-c", "-d"));
	}

	public static List<String> getCommands() {
		return new LinkedList<String>(commandMap.keySet());
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws Exception {

		HadoopConsole hConsole = new HadoopConsole();

		System.setProperty("jline.WindowsTerminal.directConsole", "false");
		ConsoleReader reader = new ConsoleReader();
		reader.setBellEnabled(false);
		reader.setDebug(new PrintWriter(new FileWriter("writer.debug", true)));

		History history = new History(new File(historyFile));
		reader.setHistory(history);

		List completors = new LinkedList();
		completors.add(new HadoopCompletor(hConsole));
		completors.add(new ArgsCompletor(hConsole, reader));
		completors.add(new FileNameCompletor());

		reader.addCompletor(new ArgumentCompletor(completors));

		String line;
		PrintWriter out = new PrintWriter(System.out);

		FsShell shell = new FsShell();
		while ((line = reader.readLine("hadoop> ")) != null) {

			if (StringUtils.isEmpty(line)) {
				continue;
			}

			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				break;
			}

			String[] cmds = line.split("\\s");
			runCmds(cmds, shell);
		}

	}

	private static void runCmds(String[] cmds, FsShell shell) {

		if (StringUtils.equals(cmds[0], "fs")) {
			int res;
			try {
				res = ToolRunner.run(shell,
						Arrays.copyOfRange(cmds, 1, cmds.length));
			} catch (Exception e) {
				// TODO 当前不进行处理
				e.printStackTrace();
			}
		}
	}

}
