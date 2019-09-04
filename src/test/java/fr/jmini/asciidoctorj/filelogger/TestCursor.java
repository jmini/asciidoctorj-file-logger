package fr.jmini.asciidoctorj.filelogger;

import org.asciidoctor.ast.Cursor;

class TestCursor implements Cursor {

    private int lineNumber;
    private String path;
    private String dir;
    private String file;

    TestCursor(int lineNumber, String path, String dir, String file) {
        this.lineNumber = lineNumber;
        this.path = path;
        this.dir = dir;
        this.file = file;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getDir() {
        return dir;
    }

    @Override
    public String getFile() {
        return file;
    }

}
