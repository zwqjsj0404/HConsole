package org.ccnt.hadoop;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.history.FileHistory;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.util.ToolRunner;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * the console for hadoop
 * 
 * @author gaoxiao
 * 
 */
public class HadoopConsole {

	public static final Map<String, ArrayList<String>> commandMap = new HashMap<String, ArrayList<String>>();

	private static FsShell shell = new FsShell(new Configuration());

	public static FsShell getShell() {
		return shell;
	}

	static final String HISTORYFILE = ".hadoop_history";
	static String historyFile = System.getProperty("user.home")
			+ File.separator + HISTORYFILE;

	static {
		commandMap.put("fs", Lists.newArrayList("-fs", "-conf", "-D", "-jt",
				"-files", "-libjars", "-archives", "-ls", "-lsr", "-du",
				"-dus", "-count", "-mv", "-mkdir", "-cp", "-rm", "-rmr",
				"-expunge", "-put", "-copyFromLocal", "-moveFromLocal", "-get",
				"-getmerge", "-cat", "-text", "-copyToLocal", "-chmod",
				"-chown", "-chgrp", "-help", "-touchz"));
		commandMap.put("dfsadmin", Lists.newArrayList("-report", "-safemode",
				"-saveNamespace", "-refreshNodes", "-finalizeUpgrade",
				"-upgradeProgress", "-metasave", "-refreshServiceAcl",
				"-refreshSuperUserGroupsConfiguration", "-setQuota",
				"-clrQuota", "-setSpaceQuota", "-clrSpaceQuota",
				"-setBalancerBandwidth", "-conf", "-D", "-fs", "-jt",
				"-libjars", "-archives", "-help"));
		commandMap.put("secondarynamenode", null);
		commandMap.put("datanode", null);
		commandMap.put("namenode", Lists.newArrayList("-format"));
		commandMap.put("mradmin", Lists.newArrayList("-refreshServiceAcl",
				"-refreshQueues", "-refreshUserToGroupsMappings",
				"-refreshSuperUserGroupsConfiguration", "-refreshNodes",
				"-help", "-conf", "-D", "-fs", "-jt", "-libjars", "-archives"));
		commandMap.put("balancer", null);
		commandMap.put("fetchdt", Lists.newArrayList("--webservice",
				"--cancel", "--renew", "-conf", "-D", "-fs", "-jt", "-libjars",
				"-archives"));
		commandMap.put("jobtracker", null);
		commandMap.put("pipes", Lists.newArrayList("-input", "-output", "-jar",
				"-map", "-inputformat", "-partitioner", "-reduce", "-writer",
				"-program", "-reduces", "-conf", "-D", "-fs", "-jt",
				"-libjars", "-archives"));
		commandMap.put("tasktracker", null);
		commandMap.put("historyserver", null);
		commandMap.put("job", Lists.newArrayList("-submit", "-status",
				"-counter", "-kill", "-set-priority", "-events", "-history",
				"-list", "-list-active-trackers", "-list-blacklisted-trackers",
				"-list-attempt-ids", "-kill-task", "-fail-task", "-conf", "-D",
				"-fs", "-jt", "-libjars", "-archives"));
		commandMap.put("queue", Lists.newArrayList("-list", "-info",
				"-showacls", "-conf", "-D", "-fs", "-jt", "-libjars",
				"-archives"));
		commandMap.put("version", null);
		commandMap.put("jar", Lists.newArrayList("-c", "-d"));
		commandMap.put("distcp", Lists.newArrayList("-c", "-p", "-i", "-log",
				"-m", "-overwrite", "-update", "-skipcrccheck", "-f",
				"-filelimit", "-sizelimit", "-delete", "-mapredSslConf",
				"-conf", "-D", "-fs", "-jt", "-libjars", "-archives"));
		commandMap.put("archive", Lists.newArrayList("-archiveName"));
		commandMap.put("classpath", Lists.newArrayList("-c", "-d"));
		commandMap.put("daemonlog",
				Lists.newArrayList("-getlevel", "-setlevel"));
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

		// System.setProperty("jline.WindowsTerminal.directConsole", "false");
		final ConsoleReader reader = new ConsoleReader();
		// reader.setPrompt("hadoop> ");
		reader.setPrompt("\u001B[32m@hadoop\u001B[0m> ");

		final FileHistory history = new FileHistory(new File(historyFile));
		reader.setHistory(history);
		// 处理日志
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					history.flush();
				} catch (Exception ex) {
				}
			}
		});

		// 处理信号量
		SignalHandler handler = new SignalHandler() {

			public void handle(Signal signal) {
				if (StringUtils.isEmpty(reader.getCursorBuffer().toString())) {
					System.out
							.print("\nType quit or exit, or use Ctrl + d to get out!");
					System.out.print("\n" + reader.getPrompt());
				} else {
					reader.getCursorBuffer().clear();
					System.out.print("\n" + reader.getPrompt());
				}
			}
		};
		Signal.handle(new Signal("INT"), handler);

		List completors = new LinkedList();
		completors.add(new HadoopCompletor(hConsole));
		completors.add(new ArgsCompletor(reader));
		completors.add(new PathCompleter(hConsole));
		reader.addCompleter(new ArgumentCompleter(completors));

		String line;

		// run command with args
		CommandRunner runner = new CommandRunner();
		PrintWriter out = new PrintWriter(reader.getOutput());
		out.println("\u001B[33m=======>\u001B[0m\""
				+ "to access hdfs, add h before /: 'h/', then use tab" + "\"");
		while ((line = reader.readLine()) != null) {
			if (StringUtils.isEmpty(line)) {
				continue;
			}
			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				break;
			}

			String[] cmds = handleArgs(line);

			out.println("\u001B[33m=======>\u001B[0m\""
					+ Joiner.on(" ").join(cmds) + "\"");
			out.flush();

			runner.run(cmds);
		}

	}

	private static String[] handleArgs(String line) {
		// split the empty string
		Iterable<String> cmdList = Splitter.on(" ").trimResults()
				.omitEmptyStrings().split(line);
		ArrayList<String> cmdArray = Lists.newArrayList(cmdList.iterator());
		String[] cmds = (String[]) cmdArray
				.toArray(new String[cmdArray.size()]);

		// remove the hdfs 'hs' prefix
		for (int i = 1; i < cmds.length; i++) {
			if (cmds[i].startsWith("h/")) {
				cmds[i] = cmds[i].substring(1);
			}
		}

		return cmds;
	}
}
