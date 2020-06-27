package hash;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.io.FileDeleteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.concurrent.Task;

public class DuplicateValues extends Task<Map<String, List<String>>> {
	private Map<String, String> fileHashes;
	private Map<String, List<String>> groupedHashMapByValues;
	private static Logger log = LoggerFactory.getLogger(DuplicateValues.class);

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
		int totalDeletedFiles = 0;
		log.info("~~~ DELETE FILES ~~~");
		long start = System.currentTimeMillis();
		for (Entry<String, List<String>> entry : this.groupedHashMapByValues.entrySet()) {
			log.info(entry.getKey() + " Size Of List: " + entry.getValue().size());
			if (entry.getValue().size() >= 2) {
				int deletedFilesFromList = 0;
				for (int indexDeleteFile = 1; indexDeleteFile < entry.getValue().size(); indexDeleteFile++) {
					String fileToDeletePath = entry.getValue().get(indexDeleteFile).toString();
					totalDeletedFiles++;
					deletedFilesFromList++;
					FileDeleteStrategy.FORCE.delete(new File(fileToDeletePath));
					log.info(fileToDeletePath + " " + entry.getKey());
				}
				for (Iterator<String> iter = entry.getValue().listIterator(); iter.hasNext();) {
					String a = iter.next();
					if (a != entry.getValue().get(0)) {
						iter.remove();
					}
				}
				log.info("Deleted Files From List = " + deletedFilesFromList);
			}
		}
		log.info("Total Deleted Files = " + totalDeletedFiles);
		long end = System.currentTimeMillis() - start;
		log.info("Time Spent On Deleting Files = " + TimeUnit.MILLISECONDS.toSeconds(end));
	}

	@Override
	protected Map<String, List<String>> call() {
		try {
			this.groupHashMapDuplicatesByValue();
			this.deleteDuplicateFiles();
		} catch (IOException | InterruptedException e) {
			log.info(e.getMessage());
		}
		return this.groupedHashMapByValues;
	}
}