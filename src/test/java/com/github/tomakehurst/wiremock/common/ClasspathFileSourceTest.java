package com.github.tomakehurst.wiremock.common;

import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.fileNamed;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ClasspathFileSourceTest {

    ClasspathFileSource classpathFileSource;

    @SuppressWarnings("unchecked")
    @Test
    public void listsFilesRecursivelyFromJar() {
        initForJar();

        List<TextFile> files = classpathFileSource.listFilesRecursively();

        assertThat(files, hasItems(fileNamed("pom.properties"), fileNamed("pom.xml")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listsFilesRecursivelyFromFileSystem() {
        initForFileSystem();

        List<TextFile> files = classpathFileSource.listFilesRecursively();

        assertThat(files, hasItems(
                fileNamed("one"),
                fileNamed("two"),
                fileNamed("three"),
                fileNamed("four"),
                fileNamed("five"),
                fileNamed("six")));
    }

    @Test
    public void readsBinaryFileFromJar() {
        initForJar();

        BinaryFile binaryFile = classpathFileSource.getBinaryFileNamed("guava/pom.xml");

        assertThat("Expected a non zero length file",
                binaryFile.readContents().length, greaterThan(0));
    }

    @Test
    public void readsBinaryFileFromFileSystem() {
        initForFileSystem();

        BinaryFile binaryFile = classpathFileSource.getBinaryFileNamed("subdir/deepfile.json");

        assertThat("Expected a non zero length file",
                binaryFile.readContents().length, greaterThan(0));
    }

    @Test
    public void createsChildSource() {
        initForFileSystem();

        FileSource child = classpathFileSource.child("subdir");

        assertThat(child.getPath(), is("filesource/subdir"));
    }

    @Test
    public void correctlyReportsExistence() {
        assertTrue("Expected to exist", new ClasspathFileSource("filesource/subdir").exists());
        assertTrue("Expected to exist", new ClasspathFileSource("META-INF/maven/com.google.guava").exists());
        assertFalse("Expected not to exist", new ClasspathFileSource("not/exist").exists());
    }

    void initForJar() {
        classpathFileSource = new ClasspathFileSource("META-INF/maven/com.google.guava");
    }

    private void initForFileSystem() {
        classpathFileSource = new ClasspathFileSource("filesource");
    }


}
