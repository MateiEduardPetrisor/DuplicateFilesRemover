package app;

public class DuplicateFilesRemoverLoader {
	public static void main(String[] args) {
		try {
			DuplicateFilesRemover.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}