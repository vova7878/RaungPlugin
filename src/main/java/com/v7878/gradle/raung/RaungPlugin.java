package com.v7878.gradle.raung;

import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.LibraryExtension;
import com.v7878.gradle.raung.util.StringUtils;

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
        switch (android) {
            case ApplicationExtension app -> app.getSourceSets().all(sourceSet -> {
                ((PatternFilterable) (sourceSet.getResources())).exclude(RAUNG_FILE_PATTERN);

                processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                        sourceSet.getImplementationConfigurationName(), project.files(sourceSet.getJava().getDirectories()));
            });
            case LibraryExtension lib -> lib.getSourceSets().all(sourceSet -> {
                ((PatternFilterable) (sourceSet.getResources())).exclude(RAUNG_FILE_PATTERN);

                processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                        sourceSet.getImplementationConfigurationName(), project.files(sourceSet.getJava().getDirectories()));
            });
            case null, default -> {
                var java = project.getExtensions().getByType(JavaPluginExtension.class);

                java.getSourceSets().all(sourceSet -> {
                    sourceSet.getResources().exclude(RAUNG_FILE_PATTERN);

                    processSourceSet(project, sourceSet.getName(), sourceSet.getApiConfigurationName(),
                            sourceSet.getImplementationConfigurationName(), sourceSet.getJava().getSourceDirectories());
                });
            }
        }
    }

    private static void processSourceSet(Project project, String name,
                                         String apiConfig, String implConfig,
                                         FileCollection raungSourceDir) {
        var buildDir = project.getLayout().getBuildDirectory();
        var jarDir = buildDir.dir("intermediates/compile_raung/jar/" + name);
        var classesDir = buildDir.dir("intermediates/compile_raung/classes/" + name);

        var compileTask = project.getTasks().register(
                StringUtils.computeTaskName("compile", name, "raung"),
                RaungTask.class, task -> {
                    task.setDescription("Compiles Raung files for " + name + " source set");

                    task.getSourceDirectory().from(raungSourceDir);
                    task.getDestinationDirectory().set(classesDir);
                }
        );

        var jarTask = project.getTasks().register(
                StringUtils.computeTaskName("jar", name, "raung"),
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
