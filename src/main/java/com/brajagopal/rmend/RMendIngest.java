package com.brajagopal.rmend;

import com.brajagopal.rmend.enrich.CalaisClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author <bxr4261>
 */
public class RMendIngest {

    private static Logger logger = Logger.getLogger(CalaisClient.class);

    public static void main(String[] args) {
        checkUsage(args);
        CalaisClient caliasClient = null;
        try {
            caliasClient = new CalaisClient(
                    new File(args[0]),
                    new File(args[1]));

        } catch (IOException e) {
            logger.error(e);
        }

        caliasClient.run();
    }

    private static void checkUsage(String[] args) {
        if (args.length == 0) {
            printUsage("Empty Params");
        }
        else if (args.length < 2) {
            printUsage("Invalid Number of Params");
        }
        else {
            if (!new File(args[0]).exists()) {
                printUsage("File " + args[0] + " does not exist");
            }
            File outputDir = new File(args[1]);
            if (!outputDir.exists() && !outputDir.mkdirs())
                printUsage("Could not create output directory");
        }
    }

    private static void printUsage(String msg) {
        logger.error(msg);
        System.out.print(
                "Usage: java " +
                        (new Object() {
                        }.getClass().getEnclosingClass()).getName() +
                        " <input-path> <output-path>"
        );
    }
}
