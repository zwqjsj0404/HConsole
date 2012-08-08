package org.ccnt.hadoop;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;

import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;

public class PathCompleter implements Completer {

	private HadoopConsole hadoopConsole;

	public PathCompleter(HadoopConsole hConsole) {
		hadoopConsole = hConsole;
	}

	@Override
	public int complete(String buffer, int cursor, List candidates) {
		// ArgumentCompleter argumentCompleter = (ArgumentCompleter) reader
		// .getCompleters().toArray()[0];
		// argumentCompleter.getCompleters().add(new FileNameCompleter());

		// Guarantee that the final token is the one we're expanding
		if (buffer == null) {
			buffer = "";
		}
		buffer = buffer.substring(0, cursor);
		String token = "";
		if (!buffer.endsWith(" ")) {
			String[] tokens = buffer.split(" ");
			if (tokens.length != 0) {
				token = tokens[tokens.length - 1];
			}
		}

		if (token.startsWith("h/")) {
			return completeHDFSPath(buffer, token, candidates);
		}
		return completeLocalPath(buffer, token, cursor, candidates);

	}

	private int completeLocalPath(String buffer, String token, int cursor,
			List<CharSequence> candidates) {
		FileNameCompleter fileNameCompleter = new FileNameCompleter();
		return fileNameCompleter.complete(buffer, cursor, candidates);
	}

	private int completeHDFSPath(String buffer, String token,
			List<String> candidates) {
		FsShell fsShell = HadoopConsole.getShell();
		FileSystem fs = null;
		try {
			fs = FileSystem.get(fsShell.getConf());

			// get the prefix of filename
			String path = token;
			int idx = path.lastIndexOf("/") + 1;
			String prefix = path.substring(idx);

			// get the dir of the filepath.
			// Only the root path can end in a f/, so strip it off every other
			// prefix
			String dir = idx == 2 ? "/" : path.substring(1, idx - 1);
			// System.out.println("\n" + dir);

			Path dirPath = new Path(dir);

			FileStatus rootStatus = fs.getFileStatus(dirPath);
			FileStatus[] fileStatus = shellListStatus(fs, rootStatus);
			for (FileStatus status : fileStatus) {
				String child = status.getPath().getName();
				if (status.isDir()) {
					child += "/";
				}

				// match the prefix of filename
				if (child.startsWith(prefix)) {
					candidates.add(child);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return 0;
		}

		return candidates.size() == 0 ? buffer.length() : buffer
				.lastIndexOf("/") + 1;
	}

	/**
	 * helper returns listStatus()
	 * 
	 * @throws IOException
	 */
	private static FileStatus[] shellListStatus(FileSystem srcFs, FileStatus src)
			throws IOException {
		if (!src.isDir()) {
			FileStatus[] files = { src };
			return files;
		}
		Path path = src.getPath();
		FileStatus[] files = srcFs.listStatus(path);
		return files;
	}

}
