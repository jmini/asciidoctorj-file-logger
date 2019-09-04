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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.Cursor;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;
import org.junit.jupiter.api.Test;

public class FileLoggerTest {

    private static final String ADOC_FILE = "src/test/resources/test.adoc";

    @Test
    public void testLogger() throws Exception {
        TestLogger logHandler = new TestLogger();

        Asciidoctor asciidoctor = Factory.create();
        asciidoctor.registerLogHandler(logHandler);
        HashMap<String, Object> attributes = new java.util.HashMap<String, Object>();
        attributes.put("attribute-missing", "warn");

        String html = asciidoctor.convertFile(new File(ADOC_FILE), OptionsBuilder.options()
                .safe(SafeMode.UNSAFE)
                .attributes(attributes)
                .toFile(false)
                .get());

        Path path = Paths.get(ADOC_FILE)
                .toAbsolutePath();

        assertThat(html).isNotEmpty();

        assertThat(logHandler.getEntries()).containsExactly(
                "{\"fileName\":\"" + path + "\",\"lineStart\":3,\"message\":\"list item index: expected 1, got 8\",\"category\":\"Asciidoctor\",\"severity\":\"HIGH\"}\n",
                "{\"fileName\":\"" + path + "\",\"lineStart\":4,\"message\":\"list item index: expected 2, got 5\",\"category\":\"Asciidoctor\",\"severity\":\"HIGH\"}\n",
                "{\"message\":\"skipping reference to missing attribute: bla\",\"category\":\"Asciidoctor\",\"severity\":\"HIGH\"}\n");
    }

    @Test
    public void testExampleOutput() throws Exception {
        TestLogger logHandler = new TestLogger();
        Cursor cursor1 = new TestCursor(5, null, null, "/tmp/file.adoc");
        LogRecord record1 = new LogRecord(Severity.ERROR, cursor1, "include file not found: /tmp/other.adoc");
        logHandler.log(record1);

        Cursor cursor2 = new TestCursor(7, null, null, "/tmp/file.adoc");
        LogRecord record2 = new LogRecord(Severity.WARN, cursor2, "list item index: expected 1, got 8");
        logHandler.log(record2);

        LogRecord record3 = new LogRecord(Severity.WARN, "skipping reference to missing attribute: bla");
        logHandler.log(record3);

        String content = FileLogger.getCommentHeader("2019-09-04T11:10:32.126Z") + String.join("", logHandler.getEntries());

        Path exampleLogPath = Paths.get(getClass()
                .getClassLoader()
                .getResource("example-log.txt")
                .toURI());
        String expectedContent = new String(Files.readAllBytes(exampleLogPath));

        assertThat(content).isEqualTo(expectedContent);
    }
}
