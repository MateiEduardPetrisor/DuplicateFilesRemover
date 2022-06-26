# DuplicateFilesRemover

Initial version V1:
 - The application was created to remove duplicate files based in their hash value;
 - We know that a hash function is not reversible (ex: if you have the file hash you can obtain the file) and the hash value is unique for each file;
 - The application uses BouncyCastle Provider 1.58 for SHA-3 hash functions;
 - For MD5, SHA-1 and SHA-2 it uses the SUN Provider;
 - The hash algorithm can be selected by user;
 - The application save the file hash values in "FolderHashes.txt";
 - Deleted files are stored in "DeletedFiles.txt";
 - BUG: If you export the project as runnable JAR file, and try to run it on a different machine with java 8 installed, you will get an error when you try to hash folder files;
 - SOLUTION: Close the application and run it again, it should work;
 
Changes in V2:
 - Added drag and drop folder input;
 - Renamed Main class to DuplicateFilesRemover;
 - Alert window display the exception message;
 - Updated BouncyCastle to v1.59;
 - Registration of BouncyCastle is done via static block;
  
Changes in V3:
 - BouncyCastle updated to v1.70
 - Maven dependencies updated
 - Before the Jar file was packed with Maven shade plugin in big jar file. This was breaking the signature of BouncyCastle(as a workaround this was excluded to be packed inside the Jar file)
 - Now the Jar file is built with Maven jar plugin and Maven dependency plugin(all dependencies are copied in a folder and the jar file contain only the project code and resources)
 - Project built on JDK version 17