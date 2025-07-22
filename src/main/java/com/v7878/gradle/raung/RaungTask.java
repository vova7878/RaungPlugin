package com.v7878.gradle.raung;

import static com.v7878.gradle.raung.RaungPlugin.RAUNG_FILE_PATTERN;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Files;

import io.github.skylot.raung.asm.RaungAsm;

public abstract class RaungTask extends DefaultTask {
    @SkipWhenEmpty
    @InputFiles
    public abstract ConfigurableFileCollection getSourceDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getDestinationDirectory();

    @TaskAction
    public void doAction() throws Exception {
        for (File file : getDestinationDirectory().getAsFileTree()) {
            Files.delete(file.toPath());
        }

        File dstDir = getDestinationDirectory().get().getAsFile();

        getSourceDirectory()
                .getAsFileTree()
                .matching(pattern -> pattern.include(RAUNG_FILE_PATTERN))
                .visit(file -> {
                    if (!file.isDirectory()) {
                        String path = file.getPath();
                        path = path.substring(0, path.length() - 6);
                        path += ".class";
                        File outputFile = new File(dstDir, path);

                        //noinspection ResultOfMethodCallIgnored
                        outputFile.getParentFile().mkdirs();

                        getLogger().info("Compiling Raung file: {}", file);
                        try {
                            RaungAsm.create()
                                    .input(file.getFile().toPath())
                                    .output(outputFile.toPath())
                                    .execute();
                        } catch (Exception e) {
                            throw new GradleException("Failed to compile Raung file: " + file.getFile().toPath(), e);
                        }
                    }
                });
    }
}
