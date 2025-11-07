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

import static com.v7878.gradle.raung.Utils.computeTaskName;

import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.ZipEntryCompression;
import org.gradle.api.tasks.util.PatternFilterable;

@SuppressWarnings("UnstableApiUsage")
public class RaungPlugin implements Plugin<Project> {
    public static final String RAUNG_FILE_PATTERN = "**/*.raung";

    @Override
    public void apply(Project project) {
        var android = project.getExtensions().findByName("android");
        if (android instanceof ApplicationExtension app) {
            app.getSourceSets().all(sourceSet -> {
                ((PatternFilterable) (sourceSet.getResources())).exclude(RAUNG_FILE_PATTERN);

                processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                        sourceSet.getImplementationConfigurationName(), project.files(sourceSet.getJava().getDirectories()));
            });
        } else if (android instanceof LibraryExtension lib) {
            lib.getSourceSets().all(sourceSet -> {
                ((PatternFilterable) (sourceSet.getResources())).exclude(RAUNG_FILE_PATTERN);

                processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                        sourceSet.getImplementationConfigurationName(), project.files(sourceSet.getJava().getDirectories()));
            });
        } else {
            var java = project.getExtensions().getByType(JavaPluginExtension.class);

            java.getSourceSets().all(sourceSet -> {
                sourceSet.getResources().exclude(RAUNG_FILE_PATTERN);

                processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                        sourceSet.getImplementationConfigurationName(), sourceSet.getJava().getSourceDirectories());
            });
        }
    }

    private static void processSourceSet(Project project, String name,
                                         String apiConfig, String implConfig,
                                         FileCollection raungSourceDir) {
        var buildDir = project.getLayout().getBuildDirectory();
        var jarDir = buildDir.dir("intermediates/compile_raung/jar/" + name);
        var classesDir = buildDir.dir("intermediates/compile_raung/classes/" + name);

        var compileTask = project.getTasks().register(
                computeTaskName("compile", name, "raung"),
                RaungTask.class, task -> {
                    task.setDescription("Compiles Raung files for " + name + " source set");

                    task.getSourceDirectory().from(raungSourceDir);
                    task.getDestinationDirectory().set(classesDir);
                }
        );

        var jarTask = project.getTasks().register(
                computeTaskName("jar", name, "raung"),
                Jar.class, jar -> {
                    jar.getDestinationDirectory().set(jarDir);

                    jar.getArchiveBaseName().set("classes");

                    jar.setIncludeEmptyDirs(false);
                    jar.setEntryCompression(ZipEntryCompression.DEFLATED);
                    jar.setPreserveFileTimestamps(false);

                    jar.from(compileTask);
                }
        );

        var configName = project.getConfigurations().findByName(apiConfig) == null ? implConfig : apiConfig;
        project.getDependencies().add(configName, project.files(jarTask));
    }
}
