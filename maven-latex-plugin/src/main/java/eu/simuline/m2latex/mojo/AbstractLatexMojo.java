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

package eu.simuline.m2latex.mojo;

import eu.simuline.m2latex.core.LatexProcessor;
import eu.simuline.m2latex.core.ParameterAdapter;
import eu.simuline.m2latex.core.Settings;

import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Abstract base class for all mojos. 
 *
 */
abstract class AbstractLatexMojo extends AbstractMojo 
    implements ParameterAdapter {

    /**
     * The base directory of this maven project. 
     * Reinitializes {@link Settings#baseDirectory} via {@link #initialize()}. 
     */
    @Parameter(name = "baseDirectory", 
	       defaultValue = "${basedir}", 
	       readonly = true)
    protected File baseDirectory;

    /**
     * The target directory of this maven project. 
     * Reinitializes {@link Settings#targetDirectory} 
     * via {@link #initialize()}. 
     */
    @Parameter(name = "targetDirectory", 
	       defaultValue = "${project.build.directory}", 
	       readonly = true)
    protected File targetDirectory;

    /**
     * The target site directory of this maven project. 
     * Reinitializes {@link Settings#baseDirectory} via {@link #initialize()}. 
     */
    @Parameter(name = "targetSiteDirectory", 
	       defaultValue = "${project.reporting.outputDirectory}", 
	       readonly = true)
    protected File targetSiteDirectory;

    /**
     * Comrpises all parameters for executing this maven plugin. 
     * If not set in the pom prior to execution, 
     * is set in {@link #initialize()}. 
     */
    // for help plugin this does not fit. 
    // also not ideal that settings is all the same independent of target. 
    // also for help plugin this is not detailed enough. 
    @Parameter(name = "settings")
    protected Settings settings;

    // set by {@link #initialize()}. 
    protected LatexProcessor latexProcessor;


    // api-docs inherited from ParameterAdapter 
    public final void initialize() {
	if (this.settings == null) {
	    // Here, no configuration is defined in pom, 
	    // i.e. object is not created by Maven
	    this.settings = new Settings();
	}
	this.settings.setBaseDirectory(this.baseDirectory);
	this.settings.setTargetSiteDirectory(this.targetSiteDirectory);
	this.settings.setTargetDirectory(this.targetDirectory);

	this.latexProcessor = new LatexProcessor(this.settings,  
						 new MavenLogWrapper(getLog()), 
						 this);
    }
}
