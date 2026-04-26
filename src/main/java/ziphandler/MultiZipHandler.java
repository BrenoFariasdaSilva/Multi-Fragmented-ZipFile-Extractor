package ziphandler;  // Define package structure for proper project organization

import net.lingala.zip4j.ZipFile;  // Import ZipFile for ZIP extraction and creation
import net.lingala.zip4j.model.ZipParameters;  // Import ZIP configuration parameters

import java.io.File;  // File handling for filesystem operations
import java.nio.file.Files;  // Temporary directory creation utilities
import java.util.ArrayList;  // Dynamic list implementation
import java.util.List;  // List interface

public class MultiZipHandler {

    public static void main(String[] args) {

        // Verify minimum required arguments (output ZIP + at least one input ZIP)
        if (args.length < 2) {
            System.out.println("{\"status\":\"error\",\"message\":\"Invalid arguments\"}");
            System.exit(1);
        }

        // Define output ZIP path from first argument
        String outputZip = args[0];

        // Collect all input ZIP files from remaining arguments
        List<String> zipInputs = collectInputZips(args);

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

    private static List<String> collectInputZips(String[] args) {

        // Create list for storing input ZIP paths
        List<String> zipInputs = new ArrayList<>();

        // Iterate over arguments starting from index 1
        for (int i = 1; i < args.length; i++) {

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

        // Create ZipFile handler for input archive
        ZipFile zf = new ZipFile(zipFile);

        // Verify if archive is split (.z01 present)
        if (zf.isSplitArchive()) {

            // Log split archive detection
            System.out.println("[DEBUG] Split archive detected (.z01)");
        }

        try {

            // Extract ZIP contents into target directory
            zf.extractAll(extractDir.getAbsolutePath());

        } catch (Exception e) {

            // Fail extraction with contextual message
            throw new IllegalStateException("Extraction failed for: " + zipFile.getName(), e);
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
            System.out.println("[DEBUG] Adding to final ZIP: " + dir.getName());

            // Add folder content into final ZIP
            outputZip.addFolder(dir, params);
        }
    }

}