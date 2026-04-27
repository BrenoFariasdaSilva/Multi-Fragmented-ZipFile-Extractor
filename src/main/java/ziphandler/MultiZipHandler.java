package ziphandler;  // Define package structure for proper project organization

import net.lingala.zip4j.ZipFile;  // Import ZipFile for ZIP extraction and creation
import net.lingala.zip4j.exception.ZipException;  // Import exception handling for ZIP operations
import net.lingala.zip4j.model.ZipParameters;  // Import ZIP configuration parameters

import java.io.File;  // File handling for filesystem operations
import java.nio.file.Files;  // Temporary directory creation utilities
import java.util.ArrayList;  // Dynamic list implementation
import java.util.List;  // List interface

/**
 * MultiZipHandler
 *
 * Core automation class responsible for:
 * - Processing ZIP archives (including split archives)
 * - Extracting contents into temporary workspace
 * - Merging extracted data into final ZIP output
 * - Handling filesystem cleanup
 * - Providing structured logging and CLI integration
 *
 * Designed for cross-platform execution (Windows, Linux, macOS).
 */
public class MultiZipHandler {

    // =========================
    // LOG LEVEL CONTROL
    // =========================

    /**
     * Global log level configuration used to control verbosity of execution logs.
     */
    private static String LOG_LEVEL = "INFO";  // Default log level (INFO only important logs)

    /**
     * Returns true when DEBUG mode is enabled.
     */
    private static boolean isDebug() { return LOG_LEVEL.equals("DEBUG"); }  // Verify debug mode

    /**
     * Returns true when ERROR mode is active.
     */
    private static boolean isError() { return LOG_LEVEL.equals("ERROR"); }  // Verify error mode

    /**
     * Returns true when INFO-level logging is allowed.
     */
    private static boolean isInfo() { return LOG_LEVEL.equals("INFO") || isDebug(); }  // Verify info mode

    /**
     * Returns true when WARN-level logging is allowed.
     */
    private static boolean isWarn() { return LOG_LEVEL.equals("WARN") || isInfo(); }  // Verify warning mode

    /**
     * Centralized logging utility supporting multiple log levels.
     *
     * @param level   Log level (DEBUG, INFO, WARN, ERROR)
     * @param message Log message content
     */
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

    // =========================
    // PATH NORMALIZATION UTILITY
    // =========================

    /**
     * Normalize a file path to Unix-style (forward slashes).
     *
     * Behavior:
     * - Converts Windows backslashes to forward slashes
     * - Preserves protocol prefixes (http://, file://)
     * - Avoids duplicate slashes
     * - Safe for cross-platform output formatting
     *
     * @param path Input file path
     * @return Normalized Unix-style path
     */
    private static String normalizePathToUnix(String path) {

        // Return empty string if input is null or empty
        if (path == null || path.isEmpty()) {
            return "";
        }

        // Replace backslashes with forward slashes
        String normalized = path.replace("\\", "/");

        // Remove duplicate slashes except after protocol (e.g., file://, http://)
        int protocolIdx = normalized.indexOf("://");
        String prefix = "";
        String rest = normalized;

        if (protocolIdx != -1) {
            prefix = normalized.substring(0, protocolIdx + 3);
            rest = normalized.substring(protocolIdx + 3);
        }

        // Replace multiple slashes with single slash in the rest
        rest = rest.replaceAll("/{2,}", "/");

        return prefix + rest;
    }

    // =========================
    // MAIN ENTRY
    // =========================

    /**
     * Application entry point.
     *
     * Responsibilities:
     * - Parse CLI arguments
     * - Configure logging level
     * - Execute ZIP processing pipeline
     * - Output structured JSON result
     *
     * @param args CLI arguments
     */
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
            System.out.println("{\"status\":\"success\",\"output\":\"" + normalizePathToUnix(outputZip) + "\"}");
            System.exit(0);

        } catch (Exception e) {

            // Normalize error message for JSON safety
            String msg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "unknown error";

            // Output error response for Python integration
            System.out.println("{\"status\":\"error\",\"message\":\"" + msg + "\"}");
            System.exit(1);
        }
    }

    // =========================
    // CORE PIPELINE
    // =========================

    /**
     * Collects all input ZIP files from CLI arguments.
     *
     * @param args   CLI arguments
     * @param offset Argument offset index
     * @return List of ZIP file paths
     */
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

    /**
     * Main ZIP processing pipeline:
     * - Validates ZIP files
     * - Extracts contents
     * - Merges extracted directories
     * - Cleans temporary workspace
     *
     * @param zipPaths      Input ZIP file paths
     * @param outputZipPath Output ZIP path
     */
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

    // =========================
    // FILE OPERATIONS
    // =========================

    /**
     * Verifies that a ZIP file exists on disk.
     *
     * @param zipFile ZIP file reference
     */
    private static void verifyZipExists(File zipFile) {

        // Verify file existence
        if (!zipFile.exists()) {

            // Fail execution if ZIP is missing
            throw new IllegalArgumentException("ZIP not found: " + zipFile.getAbsolutePath());
        }
    }

    /**
     * Extracts base filename without .zip extension.
     *
     * @param zipFile ZIP file reference
     * @return Base filename
     */
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

    /**
     * Creates extraction directory for a ZIP archive.
     *
     * @param tempRoot Temporary root directory
     * @param baseName Base filename
     * @return Created directory
     */
    private static File createExtractionDir(File tempRoot, String baseName) {

        // Create directory object for extraction
        File dir = new File(tempRoot, baseName);

        // Create directory on filesystem
        dir.mkdirs();

        // Return created directory
        return dir;
    }

    /**
     * Extracts ZIP archive into target directory.
     *
     * @param zipFile    Input ZIP file
     * @param extractDir Output directory
     */
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

    /**
     * Merges extracted folders into final ZIP output.
     *
     * @param extractedDirs List of extracted directories
     * @param outputZipPath Output ZIP file path
     */
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

    /**
     * Recursively deletes files and directories.
     *
     * @param file Target file or directory
     */
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