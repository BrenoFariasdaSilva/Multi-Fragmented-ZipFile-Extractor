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

}