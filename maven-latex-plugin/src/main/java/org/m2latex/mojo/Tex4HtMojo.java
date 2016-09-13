/*
 * The akquinet maven-latex-plugin project
 *
 * Copyright (c) 2011 by akquinet tech@spree GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.m2latex.mojo;

import java.io.File;
import java.io.FileFilter;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Build HTML documents from LaTeX sources.
 * 
 * @goal tex4ht
 * @phase site
 */
public class Tex4HtMojo
    extends AbstractLatexMojo
{

    // implements AbstractLatexMojo#processSource(File)
    void processSource(File texFile) 
	throws CommandLineException, MojoExecutionException {
	this.latexProcessor.processTex4ht(texFile);
    }

    // implements AbstractLatexMojo#etOutputDir()
    File getOutputDir() {
	return new File(this.settings.getTempDirectory(), 
			this.settings.getTex4htOutputDirectory());
    }

    // implements AbstractLatexMojo#getFileFilter(File)
    FileFilter getFileFilter(File texFile) {
	return this.fileUtils.getTex4htOutputFileFilter(texFile);
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        initialize();
        log.debug( "Settings: " + settings.toString() );
        log.info( "settings.getOutputDirectory(): " + 
		  settings.getOutputDirectory() );

        File texDirectory = settings.getTexDirectory();

        if ( !texDirectory.exists() )
        {
            log.info( "No tex directory - skipping LaTeX processing" );
            return;
        }

        try
        {
            fileUtils.copyLatexSrcToTempDir( texDirectory, 
					     settings.getTempDirectory() );
            List<File> latexMainFiles = fileUtils
		.getLatexMainDocuments( settings.getTempDirectory() );
            for (File texFile : latexMainFiles) 
            {
		// 1st difference to LatexMojo 
                //latexProcessor.processTex4ht( texFile );
		processSource(texFile);

		// 2nd difference to LatexMojo 
		File outputDir = getOutputDir();
		// File outputDir = 
		//     new File(settings.getTempDirectory(), 
		// 	     settings.getTex4htOutputDirectory());

		File targetDir = fileUtils.getTargetDirectory
		    (texFile, settings.getTempDirectory(), outputDir);
 
		// 3rd difference to LatexMojo 
		FileFilter fileFilter = getFileFilter(texFile);
		// FileFilter fileFilter = fileUtils
		//     .getTex4htOutputFileFilter(texFile);

                fileUtils.copyOutputToTargetFolder(fileFilter,
						   texFile, 
						   outputDir, 
						   targetDir);
            }
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Error executing command", e );
        }
        finally
        {
            if ( settings.isCleanUp() )
            {
                cleanUp();
            }
        }
    }
}
