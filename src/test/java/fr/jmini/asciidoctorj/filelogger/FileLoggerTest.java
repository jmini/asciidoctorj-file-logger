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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
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
                path + "|3|HIGH|list item index: expected 1, got 8\n",
                path + "|4|HIGH|list item index: expected 2, got 5\n",
                "UNKNOWN|0|HIGH|skipping reference to missing attribute: bla\n");
    }
}
