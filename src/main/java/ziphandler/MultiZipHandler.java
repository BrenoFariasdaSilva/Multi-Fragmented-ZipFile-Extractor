package ziphandler;  // Define package structure for proper project organization

import net.lingala.zip4j.ZipFile;  // Import ZipFile for ZIP extraction and creation
import net.lingala.zip4j.exception.ZipException;  // Import exception handling for ZIP operations
import net.lingala.zip4j.model.ZipParameters;  // Import ZIP configuration parameters

import java.io.File;  // File handling for filesystem operations
import java.nio.file.Files;  // Temporary directory creation utilities
import java.util.ArrayList;  // Dynamic list implementation
import java.util.List;  // List interface

public class MultiZipHandler {
    
    // =========================
    // LOG LEVEL CONTROL
    // =========================
    private static String LOG_LEVEL = "INFO";  // Default log level (INFO only important logs)

    private static boolean isDebug() { return LOG_LEVEL.equals("DEBUG"); }  // Verify debug mode
    private static boolean isError() { return LOG_LEVEL.equals("ERROR"); }  // Verify error mode
    private static boolean isInfo() { return LOG_LEVEL.equals("INFO") || isDebug(); }  // Verify info mode
    private static boolean isWarn() { return LOG_LEVEL.equals("WARN") || isInfo(); }  // Verify warning mode

    private static void log(String level, String message) {

        if (level.equals("DEBUG") && isDebug()) {  // Verify debug-level logging
            System.out.println("[DEBUG] " + message);  // Print debug message
        }

        else if (level.equals("ERROR")) {  // Verify error-level logging
            System.out.println("[ERROR] " + message);  // Print error message
        }

        else if (level.equals("INFO") && isInfo()) {  // Verify info-level logging
            System.out.println("[INFO] " + message);  // Print info message
        }

        else if (level.equals("WARN") && isWarn()) {  // Verify warning-level logging
            System.out.println("[WARNING] " + message);  // Print warning message
        }
    }

    public static void main(String[] args) {

        // Verify minimum required arguments (output ZIP + at least one input ZIP)
        if (args.length < 2) {
            System.out.println("{\"status\":\"error\",\"message\":\"Invalid arguments\"}");
            System.exit(1);
        }

        // =========================
        // OPTIONAL LOG LEVEL ARG
        // =========================
        int argOffset = 0;  // Initialize argument offset

        if (args.length > 2 && args[0].startsWith("--log=")) {  // Verify optional log level argument
            LOG_LEVEL = args[0].substring(6).toUpperCase();  // Extract log level value
            argOffset = 1;  // Shift argument index
        }

        // Define output ZIP path from first argument
        String outputZip = args[argOffset];

        // Collect all input ZIP files from remaining arguments
        List<String> zipInputs = collectInputZips(args, argOffset);

        try {

            // Execute extraction + merge pipeline
            handleZips(zipInputs, outputZip);

            // Output success response for Python integration
            System.out.println("{\"status\":\"success\",\"output\":\"" + outputZip + "\"}");
            System.exit(0);

        } catch (Exception e) {

            // Normalize error message for JSON safety
            String msg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "unknown error";

            // Output error response for Python integration
            System.out.println("{\"status\":\"error\",\"message\":\"" + msg + "\"}");
            System.exit(1);
        }
    }

    private static List<String> collectInputZips(String[] args, int offset) {

        // Create list for storing input ZIP paths
        List<String> zipInputs = new ArrayList<>();

        // Iterate over arguments starting from index 1 (or offset)
        for (int i = offset + 1; i < args.length; i++) {

            // Add each ZIP path to the list
            zipInputs.add(args[i]);
        }

        // Return collected input ZIP list
        return zipInputs;
    }

    public static void handleZips(List<String> zipPaths, String outputZipPath) throws Exception {

        // Create temporary workspace directory for extraction process
        File tempRoot = Files.createTempDirectory("zip_merge_workspace").toFile();

        // Store extracted directories for merging phase
        List<File> extractedDirs = new ArrayList<>();

        // Iterate over all input ZIP files
        for (String zipPath : zipPaths) {

            // Convert string path into File object
            File zipFile = new File(zipPath);

            // Verify that ZIP file exists
            verifyZipExists(zipFile);

            // Extract base name from ZIP file
            String baseName = getBaseName(zipFile);

            // Create extraction directory for this ZIP
            File extractDir = createExtractionDir(tempRoot, baseName);

            // Extract ZIP contents into directory
            extractZip(zipFile, extractDir);

            // Store extracted directory for merging
            extractedDirs.add(extractDir);
        }

        // Merge all extracted folders into final ZIP
        mergeFolders(extractedDirs, outputZipPath);

        // Delete temporary workspace after completion
        deleteRecursive(tempRoot);
    }

    private static void verifyZipExists(File zipFile) {

        // Verify file existence
        if (!zipFile.exists()) {

            // Fail execution if ZIP is missing
            throw new IllegalArgumentException("ZIP not found: " + zipFile.getAbsolutePath());
        }
    }

    private static String getBaseName(File zipFile) {

        // Retrieve file name
        String name = zipFile.getName();

        // Remove .zip extension if present
        if (name.toLowerCase().endsWith(".zip")) {

            // Strip extension from name
            name = name.substring(0, name.length() - 4);
        }

        // Return cleaned base name
        return name;
    }

    private static File createExtractionDir(File tempRoot, String baseName) {

        // Create directory object for extraction
        File dir = new File(tempRoot, baseName);

        // Create directory on filesystem
        dir.mkdirs();

        // Return created directory
        return dir;
    }

    private static void extractZip(File zipFile, File extractDir) {

        try {

            // Create ZipFile handler for input archive (may throw ZipException)
            ZipFile zf = new ZipFile(zipFile);

            // Verify if archive is split (.z01 present)
            if (zf.isSplitArchive()) {
                log("DEBUG", "Split archive detected (.z01)");  // Controlled debug logging
            }

            // Extract ZIP contents into target directory
            zf.extractAll(extractDir.getAbsolutePath());

        } catch (net.lingala.zip4j.exception.ZipException e) {

            // Zip4j-specific failure
            throw new IllegalStateException(
                "ZIP processing failed: " + zipFile.getName(), e
            );

        } catch (Exception e) {

            // Any other failure
            throw new IllegalStateException(
                "Unexpected ZIP error: " + zipFile.getName(), e
            );
        }
    }

    private static void mergeFolders(List<File> extractedDirs, String outputZipPath) throws Exception {

        // Create final output ZIP handler
        ZipFile outputZip = new ZipFile(outputZipPath);

        // Create default ZIP parameters
        ZipParameters params = new ZipParameters();

        // Iterate over extracted directories
        for (File dir : extractedDirs) {

            // Log directory being added
            log("DEBUG", "Adding to final ZIP: " + dir.getName());  // Controlled logging

            // Add folder content into final ZIP
            outputZip.addFolder(dir, params);
        }
    }

    private static void deleteRecursive(File file) {

        // Verify if file is a directory
        if (file.isDirectory()) {

            // Retrieve directory contents
            File[] files = file.listFiles();

            // Verify directory is not empty
            if (files != null) {

                // Iterate through all children
                for (File sub : files) {

                    // Recursively delete child
                    deleteRecursive(sub);
                }
            }
        }

        // Delete file or empty directory
        file.delete();
    }
}