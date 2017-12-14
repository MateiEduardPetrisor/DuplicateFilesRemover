package hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.util.encoders.Hex;
import javafx.concurrent.Task;

public class HashFolder extends Task<Map<String, String>> implements InterfaceHash {

	private Path aux;
	private Path folderPath;
	private List<Path> filesPaths;
	private Map<String, String> fileHashes;
	private String hashAlgoritm;
	private boolean includeSubfolders;
	private final String fileNameForSavingHashes = "FolderHashes.txt";

	public HashFolder(Path folderPath, String hashAlgoritm, boolean includeSubfolders) {
		super();
		this.aux = folderPath;
		this.folderPath = Paths.get(this.aux.toString());
		this.hashAlgoritm = hashAlgoritm;
		this.fileHashes = new HashMap<String, String>();
		this.filesPaths = new ArrayList<Path>();
		this.includeSubfolders = includeSubfolders;
	}

	@Override
	public void computeFilesHashes() throws Exception {
		MessageDigest digest = MessageDigest.getInstance(this.hashAlgoritm);
		File file = new File(this.fileNameForSavingHashes);
		FileWriter fileWriter = new FileWriter(file, true);
		String folderLineToWrite = "Folder " + this.folderPath + System.lineSeparator();
		fileWriter.write(folderLineToWrite);
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
			String fileHashLineToWrite = path.toFile().getAbsolutePath() + " " + this.hashAlgoritm + " " + hashConverted
					+ System.lineSeparator();
			fileWriter.write(fileHashLineToWrite);
			inputStream.close();
		}
		long end = System.currentTimeMillis() - start;
		String timeElapsedLineToWrite = "Time: " + TimeUnit.MILLISECONDS.toSeconds(end) + " Seconds "
				+ System.lineSeparator();
		fileWriter.write(timeElapsedLineToWrite);
		fileWriter.close();
	}

	@Override
	public void listFilesInsideFolder() throws IOException {
		File directory = new File(this.aux.toFile().getAbsolutePath());
		this.setFileAtributtes(Paths.get(directory.getAbsolutePath()));
		File[] listOfFiles = directory.listFiles();
		for (File file : listOfFiles) {
			if ((file.isDirectory()) && (this.includeSubfolders == true)) {
				this.aux = Paths.get(file.getAbsolutePath());
				this.setFileAtributtes(Paths.get(file.getAbsolutePath()));
				this.listFilesInsideFolder();
			}
			if (file.isFile()) {
				this.setFileAtributtes(Paths.get(file.getAbsolutePath()));
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
	protected Map<String, String> call() throws Exception {
		this.listFilesInsideFolder();
		this.computeFilesHashes();
		return this.fileHashes;
	}

	@Override
	public void setFileAtributtes(Path file) throws IOException {
		Files.setAttribute(file, "dos:archive", false);
		Files.setAttribute(file, "dos:hidden", false);
		Files.setAttribute(file, "dos:readonly", false);
		Files.setAttribute(file, "dos:system", false);
		File fileItem = new File(file.toString());
		fileItem.setReadable(true);
		fileItem.setWritable(true);
		fileItem.canExecute();
		fileItem.canRead();
		fileItem.canWrite();
	}

	@Override
	public void printFileAtributes(Path path) throws IOException {
		DosFileAttributes attributes = Files.readAttributes(path, DosFileAttributes.class);
		System.out.println(path.toString());
		System.out.println("isArchive() = " + attributes.isArchive());
		System.out.println("isHidden() = " + attributes.isHidden());
		System.out.println("isReadOnly() = " + attributes.isReadOnly());
		System.out.println("isSystem() = " + attributes.isSystem());
		System.out.println("IsOther() = " + attributes.isOther());
		System.out.println("IsRegularFile() =" + attributes.isRegularFile());
		System.out.println();
	}
}