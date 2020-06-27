package hash;

public abstract class HashAlgorithms {
	private static final String MD5 = "MD5";
	private static final String SHA1 = "SHA1";
	private static final String SHA2_224 = "SHA-224";
	private static final String SHA2_256 = "SHA-256";
	private static final String SHA2_384 = "SHA-384";
	private static final String SHA2_512 = "SHA-512";
	private static final String SHA3_224 = "SHA3-224";
	private static final String SHA3_256 = "SHA3-256";
	private static final String SHA3_384 = "SHA3-384";
	private static final String SHA3_512 = "SHA3-512";

	public static String getMD5() {
		return HashAlgorithms.MD5;
	}

	public static String getSHA1() {
		return HashAlgorithms.SHA1;
	}

	public static String getSHA2_224() {
		return HashAlgorithms.SHA2_224;
	}

	public static String getSHA2_256() {
		return HashAlgorithms.SHA2_256;
	}

	public static String getSHA2_384() {
		return HashAlgorithms.SHA2_384;
	}

	public static String getSHA2_512() {
		return HashAlgorithms.SHA2_512;
	}

	public static String getSHA3_224() {
		return HashAlgorithms.SHA3_224;
	}

	public static String getSHA3_256() {
		return HashAlgorithms.SHA3_256;
	}

	public static String getSHA3_384() {
		return HashAlgorithms.SHA3_384;
	}

	public static String getSHA3_512() {
		return HashAlgorithms.SHA3_512;
	}
}