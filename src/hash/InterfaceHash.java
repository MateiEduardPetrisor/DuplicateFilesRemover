package hash;

import java.io.IOException;
import java.nio.file.Path;

public interface InterfaceHash {
	public void computeFilesHashes() throws Exception;

	public void listFilesInsideFolder() throws Exception;

	public void setFileAtributtes(Path path) throws IOException;

	public void printFileAtributes(Path path) throws IOException;
}