# SimpleFileHashUtility

Initial version V1:
 - The application was created to compute file hashes;
 - It uses the algorithms provided by SUN (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
 - For SHA-3 (SHA3-256, SHA3-384, SHA3-512) it is using the BouncyCastle(1.58);
 - Registration of BouncyCastle Provider is made in public static void main(String[] args);
 - BUG: If you export the project in a runnable JAR file and copy the jar on other computer with java 8 installed, when you first run the application, open a file and try to compute it's hash you will get an Alert window displaying an Error message. SOLUTION: Close the application and run it again, it should work.


Changes in V2;
 - The BouncyCastle Provider is registered using a static block;
 - Updated the application to support drag and drop file input;
 - Updated BouncyCastle Provider to version 1.59;
 - Renamed the Main class to SimpleFileHashUtility;
 - Alert window now display the exception message;
 - BUG: If you drag and drop a folder in the application you will get this error <FolderPath> (Access is denied). This issue doesn't affect the application functionality.
 - Made some minor changes to check if the dragged file is a folder or not, and now the error message that you will get is "You Selected A Folder!".
 - At this time the application can work with files only not with folders.