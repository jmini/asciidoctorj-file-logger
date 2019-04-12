/*********************************************************************
* Copyright (c) 2019 Jeremie Bresson
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.jmini.asciidoctorj.filelogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.asciidoctor.ast.Cursor;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

public class FileLogger implements LogHandler {

    @Override
    public void log(LogRecord logRecord) {
        Path path = Paths.get("build/asciidoctor.log");
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            Files.write(path, convertLog(logRecord).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String convertLog(LogRecord logRecord) {
        Cursor cursor = logRecord.getCursor();
        String file;
        int lineNumber;
        if (cursor == null) {
            file = "UNKNOWN";
            lineNumber = 0;
        } else {
            file = cursor.getFile();
            lineNumber = cursor.getLineNumber();
        }
        String severity = mapSeverity(logRecord.getSeverity());
        String message = logRecord.getMessage();
        return file + "|" + lineNumber + "|" + severity + "|" + message + "\n";
    }

    private static String mapSeverity(Severity severity) {
        switch (severity) {
        case DEBUG:
            return "LOW";
        case INFO:
        case UNKNOWN:
            return "NORMAL";
        case ERROR:
        case FATAL:
            return "ERROR";
        case WARN:
        default:
            return "HIGH";
        }
    }

}
