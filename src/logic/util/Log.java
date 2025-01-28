package logic.util;

import java.io.PrintStream;
import java.util.Locale;

import static logic.util.ConsoleColor.*;

/**
 * Utility class with static methods for simplified but detailed console output
 * To enable debug output, you have to set the environment variable "VERBOSE" to "true";
 *
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class Log {
    /**
     * Debug Settings
     */
    private static final PrintStream STD_OUT = System.out;

    /**
     * Warning Settings
     */
    private static final PrintStream STD_WARNING = System.out;
    /**
     * Warning Prefix
     */
    private static final String STD_WARNING_PREFIX = "[WARNING] ";

    /**
     * Error Settings
     */
    private static final PrintStream STD_ERR = System.err;
    /**
     * Error Prefix
     */
    private static final String STD_ERR_PREFIX = "[ERROR] ";

    /**
     * Standard Settings
     */
    private static final String STD_FORMAT = "%s%n";
    /**
     * Stacktrace depth
     */
    private static final int STD_TRACE_DEPTH = 3;

    /**
     * Global static const, true if env "VERBOSE" equals "true"
     */
    public static final boolean VERBOSE = System.getenv("VERBOSE") != null
            && System.getenv("VERBOSE").toLowerCase(Locale.ROOT)
                .equals("true");

    /**
     * if debug mode is enabled, prints messages to STD_OUT
     *
     * @param msg the msg
     */
    public static void debug(Object msg) {
        print(STD_OUT, getLocation(),STD_FORMAT, msg);
    }

    /**
     * if debug mode is enabled, prints messages to STD_OUT
     *
     * @param format the format
     * @param args   the args
     */
    public static void debug(String format, Object... args) {
        print(STD_OUT, getLocation(), format, args);
    }

    /**
     * Log Warning
     * @param msg message object
     */
    public static void warning(Object msg) {
        print(STD_WARNING, getLocation(), YELLOW_BOLD + STD_WARNING_PREFIX + STD_FORMAT, msg);
    }

    /**
     * Log warning
     * @param format format string
     * @param args replace objects
     */
    public static void warning(String format, Object... args) {
        print(STD_WARNING, getLocation(), YELLOW_BOLD + STD_WARNING_PREFIX + format, args);
    }

    /**
     * Log exception or diffrent object / message
     * @param msg message object
     */
    public static void error(Object msg) {
        if (msg instanceof Exception e) {
            print(STD_ERR,
                    getLocation(),
                    "" + RED_BOLD + STD_ERR_PREFIX +
                            "> %s | %s %n",
                    e.getClass(),
                    e.getMessage());
        } else {
            print(STD_ERR, getLocation(), RED_BOLD + STD_ERR_PREFIX + STD_FORMAT, msg);
        }
    }

    /**
     * Log message with placeholders
     * @param format format string
     * @param args arguments
     */
    public static void error(String format, Object... args) {
        print(STD_ERR, getLocation(), RED_BOLD + STD_ERR_PREFIX + format, args);
    }

    /**
     * Procedure to printf on <b>out</b> with <b>classname</b>
     * @param out PrintStream
     * @param classname Name of source
     * @param format string format
     * @param args args for format
     */
    private static void print(PrintStream out, String classname, String format, Object... args) {
        if (VERBOSE) {
            out.printf(WHITE + "[%s] ", java.time.Clock.systemUTC().instant());
            out.printf(WHITE + "%s > ", classname);
            out.printf(RESET + BOLD.toString() + format + RESET, args);
        }
    }

    /**
     * Get location string
     * @return location string
     */
    private static String getLocation() {
        return getLocation(STD_TRACE_DEPTH);
    }

    /**
     * Get location of custom depth
     * @param depth depth
     * @return location string
     */
    private static String getLocation(int depth) {
        return getLocation(depth, new Exception());
    }

    /**
     * Get location of custom depth
     * @param depth depth
     * @param exception new exception
     * @return location string
     */
    private static String getLocation(int depth, Exception exception) {
        if (VERBOSE) {
            var stackTrace = exception.getStackTrace();
            var caller = stackTrace[depth];

            return String.format("(%s:%s) %s#%s",
                    caller.getFileName(),
                    caller.getLineNumber(),
                    caller.getClassName(),
                    caller.getMethodName()
            );
        } else {
            return "";
        }
    }
}
