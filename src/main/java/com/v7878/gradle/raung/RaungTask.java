/*
 * Copyright (c) 2025 Vladimir Kozelkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.v7878.gradle.raung;

import static com.v7878.gradle.raung.RaungPlugin.RAUNG_FILE_PATTERN;
import static com.v7878.gradle.raung.Utils.files;

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
                .visit(files(file -> {
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
                }));
    }
}
