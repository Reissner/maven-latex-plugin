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

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * The settings for this plugin. 
 * These are the elements of the pom in element <code>settings</code>
 */
public class Settings
{

   private final static String SST;

    static {
	String fs = System.getProperty("file.separator");
	SST = "src" + fs + "site" + fs + "tex";
    }

    /**
     * @parameter
     * @readonly
     */
    private File baseDirectory;

    /**
     * @parameter
     * @readonly
     */
    private File targetSiteDirectory;

    /**
     * @parameter
     * @readonly
     */
    private File targetDirectory;

    /**
     * The working directory as a string, 
     * for temporary files and LaTeX processing 
     * relative to ${project.build.directory}. 
     * The according file is given by {@link #tempDirectoryFile}. 
     * **** MAYBE NOT COMPLETELY CORRECT. 
     *
     * @parameter
     * @readonly
     */
    private String tempDirectory = null;

    /**
     * All tex main documents in this folder (including subfolders) 
     * will be processed. 
     * Getter method {@link #getTexDirectory()} 
     * implements default value <code>baseDirectory/src/site/tex</code> 
     * for unix and accordingly for windows. 
     *
     * @parameter
     */
    private File texDirectory = null;

    /**
     * The generated artifacts will be copied to this folder 
     * relative to ${project.reporting.outputDirectory}. 
     * Getter method {@link #getOutputDirectory()} 
     * implements default value {@link #targetSiteDirectory}. 
     * The according file is given by {@link #outputDirectoryFile}. 
     * **** MAYBE NOT COMPLETELY CORRECT. 
    *
     * @parameter
     */
    private String outputDirectory = null;

    /**
     * Path to the TeX scripts, if none, it must be on the system path. 
     *
     * @parameter
     */
    private File texPath = null;

    /**
     * The latex command. Default is <code>pdflatex</code>. 
     *
     * @parameter
     */
    private String texCommand = "pdflatex";

    /**
     * The tex4ht command. Default is <code>htlatex</code>. 
     *
     * @parameter
     */
    private String tex4htCommand = "htlatex";

    /**
     * The bibtex command. Default is <code>bibtex</code>. 
     *
     * @parameter
     */
    private String bibtexCommand = "bibtex";

    /**
     * Clean up the working directory in the end? 
     * May be used for debugging when setting to <code>false</code>. 
     * Default value is <code>true</code>. 
     *
     * @parameter
     */
    private boolean cleanUp = true;

    /**
     * The arguments to use when calling latex. 
     * Default are the arguments <code>-interaction=nonstopmode</code> and 
     * <code>--src-specials</code>. 
     *
     * @parameter
     */
    private String[] texCommandArgs = new String[]{
	"-interaction=nonstopmode",
	"--src-specials"
    };

    /**
     * The arguments to use when calling tex4ht. 
     * Default are <code>html,2</code>, <code></code>, <code></code>
     * and the arguments from latex, 
     * <code>-interaction=nonstopmode --src-specials</code>. 
     * <p>
     * TODO move to different fields; take latex args from texCommandArgs
     *
     * @parameter
     */
    private String[] tex4htCommandArgs = new String[] {
	"html,2", "", "", "-interaction=nonstopmode --src-specials"
    };

    /**
     * File for {@link #outputDirectory}. 
     */
    private File outputDirectoryFile = null;

    /**
     * File for {@link #tempDirectory}. 
     */
    private File tempDirectoryFile = null;


    // getter methods partially implementing default values. 


    public File getBaseDirectory()
    {
        return baseDirectory;
    }

    public String getBibtexCommand()
    {
        return bibtexCommand;
    }

   /**
     * Getter method for parameter {@link #outputDirectory} 
     * implemeting default value <code>baseDirectory/src/site/tex</code> 
     * for unix and accordingly for windows. 
     */
    public File getOutputDirectory()
    {
        if ( outputDirectoryFile == null )
	    {
		outputDirectoryFile = StringUtils.isEmpty( outputDirectory )
		    ? targetSiteDirectory
		    : new File( targetSiteDirectory, outputDirectory );
	    }
        return outputDirectoryFile;
    }

    public File getTargetDirectory()
    {
        return targetDirectory;
    }

    public File getTargetSiteDirectory()
    {
        return targetSiteDirectory;
    }

    /**
     * Getter method for parameter {@link #tempDirectory} 
     * implemeting default value <code>baseDirectory/src/site/tex</code> 
     * for unix and accordingly for windows. 
     */
    public File getTempDirectory()
    {
        if (tempDirectoryFile == null)
	    {
		String dirName = StringUtils.isBlank( tempDirectory ) 
		    ? "m2latex" 
		    : tempDirectory;
		tempDirectoryFile = new File( targetDirectory, dirName );
	    }
        return tempDirectoryFile;
    }

    public String getTex4htCommand()
    {
        return tex4htCommand;
    }

    public String[] getTex4htCommandArgs()
    {
        return tex4htCommandArgs;
    }

    public String getTexCommand()
    {
        return texCommand;
    }

    public String[] getTexCommandArgs()
    {
        return texCommandArgs;
    }


    /**
     * Getter method for parameter {@link #texDirectory} 
     * implemeting default value <code>baseDirectory/src/site/tex</code> 
     * for unix and accordingly for windows. 
     */
    public File getTexDirectory()
    {
        if ( texDirectory == null )
	    {
		texDirectory = new File(baseDirectory, SST );
	    }
        return texDirectory;
    }

    public File getTexPath()
    {
        return texPath;
    }

    public boolean isCleanUp()
    {
        return cleanUp;
    }

    // setter methods 

    public Settings setBaseDirectory( File baseDirectory )
    {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public Settings setBibtexCommand( String bibtexCommand )
    {
        this.bibtexCommand = bibtexCommand;
        return this;
    }

    public Settings setCleanUp( boolean cleanUp )
    {
        this.cleanUp = cleanUp;
        return this;
    }

    public Settings setOutputDirectory( String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public Settings setTargetDirectory( File targetDirectory )
    {
        this.targetDirectory = targetDirectory;
        return this;
    }

    public Settings setTargetSiteDirectory( File targetSiteDirectory )
    {
        this.targetSiteDirectory = targetSiteDirectory;
        return this;
    }

    public Settings setTempDirectory( String tempDirectory )
    {
        this.tempDirectory = tempDirectory;
        return this;
    }

    // **** why this with annotation parameter? 
    /**
     * @parameter
     */
    public Settings setTex4htCommand( String tex4htCommand )
    {
        this.tex4htCommand = tex4htCommand;
        return this;
    }

    public Settings setTex4htCommandArgs( String[] tex4htCommandArgs )
    {
        this.tex4htCommandArgs = tex4htCommandArgs;
        return this;
    }

    public Settings setTexCommand( String texCommand )
    {
        this.texCommand = texCommand;
        return this;
    }

    public Settings setTexCommandArgs( String[] args )
    {
        this.texCommandArgs = args;
        return this;
    }

    public Settings setTexDirectory( File texDirectory )
    {
        this.texDirectory = texDirectory;
        return this;
    }

    public Settings setTexPath( File texPath )
    {
        this.texPath = texPath;
        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append("tempDirectory=").append( tempDirectory );
        sb.append(",texPath=").append( texPath );
        sb.append(",texCommand=").append( texCommand );
        sb.append(",bibtexCommand=").append( bibtexCommand );
        sb.append(",baseDirectory=").append( baseDirectory );
        sb.append(",targetSiteDirectory=").append( targetSiteDirectory );
        sb.append(",texDirectory=").append( texDirectory );
        sb.append(",texCommandArgs=").append(Arrays.asList(texCommandArgs));
        sb.append(']');
        return sb.toString();
    }
}
