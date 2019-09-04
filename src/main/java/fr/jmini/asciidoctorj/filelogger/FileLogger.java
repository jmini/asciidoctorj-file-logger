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
import java.time.Instant;

import org.asciidoctor.ast.Cursor;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

import com.google.gson.Gson;

import fr.jmini.utils.issuemodel.Issue;

public class FileLogger implements LogHandler {

    private static Gson gson = new Gson();

    public FileLogger() {
        Path path = Paths.get("build/asciidoctor.log");
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            String timestamp = Instant.now()
                    .toString();
            Files.write(path, getCommentHeader(timestamp).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getCommentHeader(String timestamp) {
        return "# AsciidoctorJ file-logger - " + timestamp + " #\n";
    }

    @Override
    public void log(LogRecord logRecord) {
        Path path = Paths.get("build/asciidoctor.log");
        try {
            Files.write(path, convertLog(logRecord).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String convertLog(LogRecord logRecord) {
        Issue issue = new Issue();

        Cursor cursor = logRecord.getCursor();
        if (cursor != null) {
            issue.setFileName(cursor.getFile());
            issue.setLineStart(cursor.getLineNumber());
        }
        issue.setSeverity(mapSeverity(logRecord.getSeverity()));
        issue.setMessage(logRecord.getMessage());
        issue.setCategory("Asciidoctor");
        return gson.toJson(issue) + "\n";
    }

    private static fr.jmini.utils.issuemodel.Severity mapSeverity(Severity severity) {
        switch (severity) {
        case DEBUG:
            return fr.jmini.utils.issuemodel.Severity.LOW;
        case INFO:
        case UNKNOWN:
            return fr.jmini.utils.issuemodel.Severity.NORMAL;
        case ERROR:
        case FATAL:
            return fr.jmini.utils.issuemodel.Severity.ERROR;
        case WARN:
        default:
            return fr.jmini.utils.issuemodel.Severity.HIGH;
        }
    }

}
