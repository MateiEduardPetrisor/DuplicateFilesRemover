package hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.concurrent.Task;

public class HashFolder extends Task<Map<String, String>> {

	private Path aux;
	private Path folderPath;
	private List<Path> filesPaths;
	private Map<String, String> fileHashes;
	private String hashAlgoritm;
	private boolean includeSubfolders;
	private static Logger log = LoggerFactory.getLogger(HashFolder.class);

	public HashFolder(Path folderPath, String hashAlgoritm, boolean includeSubfolders) {
		super();
		this.aux = folderPath;
		this.folderPath = Paths.get(this.aux.toString());
		this.hashAlgoritm = hashAlgoritm;
		this.fileHashes = new HashMap<String, String>();
		this.filesPaths = new ArrayList<Path>();
		this.includeSubfolders = includeSubfolders;
	}

	public void computeFilesHashes() throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance(this.hashAlgoritm);
		log.info("Folder To Hash" + this.folderPath);
		long start = System.currentTimeMillis();
		for (Path path : this.filesPaths) {
			InputStream inputStream = new FileInputStream(path.toFile());
			byte[] block = new byte[4096];
			int length;
			while ((length = inputStream.read(block)) > 0) {
				digest.update(block, 0, length);
			}
			String hashConverted = convertBytesToString(digest.digest());
			this.fileHashes.put(path.toFile().getAbsolutePath(), hashConverted);
			String fileHashLineToWrite = path.toFile().getAbsolutePath() + " " + this.hashAlgoritm + " = "
					+ hashConverted;
			log.info(fileHashLineToWrite);
			inputStream.close();
		}
		long end = System.currentTimeMillis() - start;
		log.info("Time Spent On Hasing Folder = " + TimeUnit.MILLISECONDS.toSeconds(end));
	}

	public void listFilesInsideFolder() throws IOException {
		File directory = new File(this.aux.toFile().getAbsolutePath());
		File[] listOfFiles = directory.listFiles();
		for (File file : listOfFiles) {
			if ((file.isDirectory()) && (this.includeSubfolders == true)) {
				this.aux = Paths.get(file.getAbsolutePath());
				this.listFilesInsideFolder();
			}
			if (file.isFile()) {
				this.filesPaths.add(Paths.get(file.getAbsoluteFile().toString()));
			}
		}
	}

	private String convertBytesToString(byte[] bytes) {
		return Hex.toHexString(bytes);
	}

	@Override
	public String toString() {
		return this.hashAlgoritm + " " + this.aux;
	}

	@Override
	protected Map<String, String> call() {
		try {
			this.listFilesInsideFolder();
			this.computeFilesHashes();
			return this.fileHashes;
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error(e.getMessage());
		}
		return fileHashes;
	}
}