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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;

public class TestLogger implements LogHandler {

    private List<String> entries = new ArrayList<>();

    @Override
    public void log(LogRecord logRecord) {
        entries.add(FileLogger.convertLog(logRecord));
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
