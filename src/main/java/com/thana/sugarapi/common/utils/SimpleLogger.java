package com.thana.sugarapi.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SimpleLogger {

    private static final Marker INFO = MarkerManager.getMarker("INFO");
    private static final Marker ERROR = MarkerManager.getMarker("ERROR");
    private static final Marker WARNING = MarkerManager.getMarker("WARNING");
    private static final Marker DEBUG = MarkerManager.getMarker("DEBUG");

    private final String name;
    private final Logger logger;

    public SimpleLogger(String name) {
        this.name = name;
        this.logger = LogManager.getLogger(name);
    }

    public void info(String text) {
        this.logger.info(INFO, text);
    }

    public void info(String text, Object... params) {
        this.logger.info(INFO, text, params);
    }

    public void debug(String text) {
        this.logger.debug(DEBUG, text);
    }

    public void debug(String text, Object... params) {
        this.logger.info(DEBUG, text, params);
    }

    public void warning(String text) {
        this.logger.debug(WARNING, text);
    }

    public void warning(String text, Object... params) {
        this.logger.info(WARNING, text, params);
    }

    public void error(String text) {
        this.logger.info(ERROR, text);
    }

    public void error(String text, Object... params) {
        this.logger.info(ERROR, text, params);
    }

    @Override
    public String toString() {
        return String.format("[Logger/%s]", this.name);
    }
}
