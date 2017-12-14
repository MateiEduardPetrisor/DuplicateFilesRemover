package hash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.io.FileDeleteStrategy;
import javafx.concurrent.Task;

public class DuplicateValues extends Task<Map<String, List<String>>> {
	private Map<String, String> fileHashes;
	private Map<String, List<String>> groupedHashMapByValues;
	private final String fileNameForWritingDeletedFiles = "DeletedFiles.txt";

	public DuplicateValues(Map<String, String> fileHashes) {
		super();
		this.fileHashes = fileHashes;
		this.groupedHashMapByValues = new HashMap<String, List<String>>();
	}

	private void groupHashMapDuplicatesByValue() {
		Map<String, List<String>> duplicateValuesHashMap = (Map<String, List<String>>) this.fileHashes.entrySet()
				.stream().collect(Collectors.groupingBy(Map.Entry::getValue,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
		this.groupedHashMapByValues = duplicateValuesHashMap;
	}

	private void deleteDuplicateFiles() throws IOException, InterruptedException {
		File file = new File(this.fileNameForWritingDeletedFiles);
		FileWriter fileWriter = new FileWriter(file, true);
		int totalDeletedFiles = 0;
		String folderLineToWrite = "~~~ DELETE STATS ~~~" + System.lineSeparator();
		fileWriter.write(folderLineToWrite);
		long start = System.currentTimeMillis();
		for (Entry<String, List<String>> entry : this.groupedHashMapByValues.entrySet()) {
			String sizeOfTheListLineToWrite = entry.getKey() + " Size Of List: " + entry.getValue().size()
					+ System.lineSeparator();
			fileWriter.write(sizeOfTheListLineToWrite);
			if (entry.getValue().size() >= 2) {
				int deletedFilesFromList = 0;
				for (int indexDeleteFile = 1; indexDeleteFile < entry.getValue().size(); indexDeleteFile++) {
					String fileToDeletePath = entry.getValue().get(indexDeleteFile).toString();
					totalDeletedFiles++;
					deletedFilesFromList++;
					String deletedFileLineToWrite = fileToDeletePath + " " + entry.getKey() + System.lineSeparator();
					fileWriter.write(deletedFileLineToWrite);
					FileDeleteStrategy.FORCE.delete(new File(fileToDeletePath));
				}
				for (Iterator<String> iter = entry.getValue().listIterator(); iter.hasNext();) {
					String a = iter.next();
					if (a != entry.getValue().get(0)) {
						iter.remove();
					}
				}
				String deletedFileFromListLineToWrite = "Deleted Files From List: " + deletedFilesFromList
						+ System.lineSeparator();
				fileWriter.write(deletedFileFromListLineToWrite);
			}
		}
		String reportLine = "Number Of Deleted Files: " + totalDeletedFiles + System.lineSeparator();
		fileWriter.write(reportLine);
		long end = System.currentTimeMillis() - start;
		String timeElapsedLineToWrite = "Time: " + TimeUnit.MILLISECONDS.toSeconds(end) + " Seconds"
				+ System.lineSeparator();
		fileWriter.write(timeElapsedLineToWrite);
		fileWriter.close();
	}

	@Override
	protected Map<String, List<String>> call() throws Exception {
		this.groupHashMapDuplicatesByValue();
		this.deleteDuplicateFiles();
		return this.groupedHashMapByValues;
	}
}