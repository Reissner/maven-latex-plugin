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

package org.m2latex.core;

import java.io.File;
import java.io.FileFilter;

import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

/**
 * The latex pre-processor is for preprocessing graphic files 
 * in formats which cannot be included directly into a latex-file 
 * and in finding the latex main files 
 * which is done in {@link #processGraphicsSelectMain(Collection)} 
 * and in clearing the created files from the latex source directory 
 * in {@link #clearCreated(Collection)}. 
 */
public class LatexPreProcessor extends AbstractLatexProcessor {

    /**
     * Maps the suffix to the according handler. 
     * If the handler is <code>null</code>, there is no handler. 
     */
    private final static Map<String, SuffixHandler> SUFFIX2HANDLER = 
	new HashMap<String, SuffixHandler>();

    static {
	for (SuffixHandler handler : SuffixHandler.values()) {
	    SUFFIX2HANDLER.put(handler.getSuffix(), handler);
	}
    } // static 

  
    // used in preprocessing only 
    private final static String SUFFIX_TEX = ".tex";

    // home-brewed ending to represent tex including postscript 
    private final static String SUFFIX_PTX = ".ptx";
    // the next two for preprocessing and in LatexDev only 
    final static String SUFFIX_PSTEX = ".pstex";
    final static String SUFFIX_PDFTEX = ".pdf_tex";

    // suffix for xfig
    private final static String SUFFIX_FIG = ".fig";
    // suffix for svg
    private final static String SUFFIX_SVG = ".svg";
    // suffix for gnuplot
    private final static String SUFFIX_PLT = ".plt";
    // suffix for metapost
    private final static String SUFFIX_MP  = ".mp";
    // from xxx.mp creates xxx1.mps, xxx.log and xxx.mpx 
    private final static String SUFFIX_MPS = ".mps";
    private final static String SUFFIX_MPX = ".mpx";

    private final static String SUFFIX_JPG = ".jpg";
    private final static String SUFFIX_PNG = ".png";

    private final Collection<File> latexMainFiles;

    LatexPreProcessor(Settings settings, 
		      CommandExecutor executor, 
		      LogWrapper log, 
		      TexFileUtils fileUtils) {
	super(settings, executor, log, fileUtils);
	this.latexMainFiles = new TreeSet<File>();
     }

    /**
     *
     */
    enum SuffixHandler {
	fig {
	    // converts a fig-file into pdf 
	    // invoking {@link #runFig2Dev(File, LatexDev)}
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.runFig2Dev(file, LatexDev.pdf);
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
		proc.clearTargetFig(file);
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_FIG;
	    }
	},
	plt {
	    // converts a gnuplot-file into pdf 
	    // invoking {@link #runGnuplot2Dev(File, LatexDev)} 
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.runGnuplot2Dev(file, LatexDev.pdf);
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
		proc.clearTargetPlt(file);
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_PLT;
	    }
	},
	mp {
	    // converts a metapost-file into mps-format 
	    // invoking {@link #runMetapost2mps(File)} 
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.runMetapost2mps(file);
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
		proc.clearTargetMp(file);
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_MP;
	    }
	},
	svg {
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.log.info("Processing svg-file " + file + 
		 	      " done implicitly in latex run. ");
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
		proc.clearTargetSvg(file);
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_SVG;
	    }
	},
	jpg {
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.log.info("No processing for jpg-file " + file + 
			      " needed. ");
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_JPG;
	    }
	},
	png {
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.log.info("No processing for png-file " + file + 
			      " needed. ");
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_PNG;
	    }
	},
	tex {
	    void transformSrc(File file, LatexPreProcessor proc) 
		throws BuildExecutionException {
		proc.addMainFile(file);
	    }
	    void clearTarget(File file, LatexPreProcessor proc)
	    	throws BuildExecutionException {
		proc.clearTargetTex(file);
	    }
	    String getSuffix() {
		return LatexPreProcessor.SUFFIX_TEX;
	    }
	};

	abstract void transformSrc(File file, LatexPreProcessor proc)
	throws BuildExecutionException;

	abstract void clearTarget(File file, LatexPreProcessor proc)
	throws BuildExecutionException;

	abstract String getSuffix();
    } // enum SuffixHandler 

    /**
     * Runs fig2dev on fig-files to generate pdf and pdf_t files. 
     * This is a quite restricted usage of fig2dev. 
     *
     * @param figFile
     *    the fig file to be processed. 
     * @throws BuildExecutionException
     *    if running the fig2dev command 
     *    returned by {@link Settings#getFig2devCommand()} failed. 
     *    This is invoked twice: once for creating the pdf-file 
     *    and once for creating the pdf_t-file. 
     * @see #create()
     */
    // used in processGraphics(File) only 
    private void runFig2Dev(File figFile, LatexDev dev) 
	throws BuildExecutionException {

	this.log.info("Processing fig-file " + figFile + ". ");
	String command = this.settings.getFig2devCommand();
	File workingDir = figFile.getParentFile();
	String[] args;

	//File pdfFile   = this.fileUtils.replaceSuffix(figFile, SUFFIX_PDF);


	// create pdf-file (graphics without text) 
	// embedded in some tex-file 

	//if (update(figFile, pdfFile)) {
 	    args = buildArgumentsFig2Pdf(dev, 
					 this.settings.getFig2devGenOptions(), 
					 this.settings.getFig2devPdfOptions(), 
					 figFile);
	    log.debug("Running " + command + 
		      " -Lpdftex  ... on " + figFile.getName() + ". ");
	    // may throw BuildExecutionException 
	    this.executor.execute(workingDir, 
				  this.settings.getTexPath(), //**** 
				  command, 
				  args);
	    //}

	    // create tex-file (text without grapics) 
	    // enclosing the pdf-file above 

  	    //if (update(figFile, pdf_tFile)) {
 	    args = buildArgumentsFig2Ptx(dev, 
					 this.settings.getFig2devGenOptions(), 
					 this.settings.getFig2devPtxOptions(), 
					 figFile);
	    log.debug("Running " + command + 
		      " -Lpdftex_t... on " + figFile.getName() + ". ");
	    // may throw BuildExecutionException 
	    this.executor.execute(workingDir, 
				  this.settings.getTexPath(), //**** 
				  command, 
				  args);
	    //}
	// no check: just warning that no output has been created. 
    }

    private String[] buildArgumentsFig2Pdf(LatexDev dev, 
					   String optionsGen, 
					   String optionsPdf, 
					   File figFile) {
	String[] optionsGenArr = optionsGen.isEmpty() 
	    ? new String[0] : optionsGen.split(" ");
	String[] optionsPdfArr = optionsPdf.isEmpty() 
	    ? new String[0] : optionsPdf.split(" ");
	int lenSum = optionsGenArr.length +optionsPdfArr.length;

	String[] args = new String[lenSum + 4];
	// language 
	args[0] = "-L";
	args[1] = dev.getXFigInTexLanguage();
	// general options 
	System.arraycopy(optionsGenArr, 0, args, 2, optionsGenArr.length);
	// language specific options 
	System.arraycopy(optionsPdfArr, 0, 
			 args, 2+optionsGenArr.length, optionsPdfArr.length);
	// input: fig-file 
        args[2+lenSum] = figFile.getName();
	// output: pdf-file 
	args[3+lenSum] = dev.getXFigInTexFile(this.fileUtils, figFile);
	return args;
    }

    private String[] buildArgumentsFig2Ptx(LatexDev dev, 
					   String optionsGen,
					   String optionsPtx,
					   File figFile) {
	String[] optionsGenArr = optionsGen.isEmpty() 
	    ? new String[0] : optionsGen.split(" ");
	String[] optionsPtxArr = optionsPtx.isEmpty() 
	    ? new String[0] : optionsPtx.split(" ");
	int lenSum = optionsGenArr.length +optionsPtxArr.length;

	String[] args = new String[lenSum + 6];
	// language 
	args[0] = "-L";
	args[1] = dev.getXFigTexLanguage();
	// general options 
	System.arraycopy(optionsGenArr, 0, 
			 args, 2, optionsGenArr.length);
	// language specific options 
	System.arraycopy(optionsPtxArr, 0, 
			 args, 2+optionsGenArr.length, optionsPtxArr.length);
	// -p pdf-file in ptx-file 
	args[2+lenSum] = "-p";
        args[3+lenSum] = dev.getXFigInTexFile(this.fileUtils, figFile);
	// input: fig-file 
        args[4+lenSum] = figFile.getName();
	// output: ptx-file 
	args[5+lenSum] = this.fileUtils.replaceSuffix(figFile, SUFFIX_PTX)
	    .getName();
	return args;
    }

    private void clearTargetFig(File figFile) 
	throws BuildExecutionException {

	this.log.info("Deleting targets of fig-file " + figFile + ". ");
	// may throw BuildExecutionException 
	this.fileUtils.replaceSuffix(figFile, SUFFIX_PTX).delete();
	this.fileUtils.replaceSuffix(figFile, SUFFIX_PDF).delete();
    }



    /**
     * Converts a gnuplot file into a tex-file with ending ptx 
     * including a pdf-file. 
     *
     * @param pltFile 
     *    the plt-file (gnuplot format) to be converted to pdf. 
     * @throws BuildExecutionException
     *    if running the ptx/pdf-conversion built in in gnuplot fails. 
     * @see #create()
     */
    // used in processGraphics(Collection) only 
    private void runGnuplot2Dev(File pltFile, LatexDev dev) 
	throws BuildExecutionException {

	this.log.info("Processing gnuplot-file " + pltFile + ". ");
	String command = this.settings.getGnuplotCommand();
	File pdfFile = this.fileUtils.replaceSuffix(pltFile, SUFFIX_PDF);
	File ptxFile = this.fileUtils.replaceSuffix(pltFile, SUFFIX_PTX);

	String[] args = new String[] {
	    "-e",   // run a command string "..." with commands sparated by ';' 
	    // 
	    "set terminal cairolatex " + dev.getGnuplotInTexLanguage() + 
	    " " + this.settings.getGnuplotOptions() + 
	    ";set output \"" + ptxFile.getName() + 
	    "\";load \"" + pltFile.getName() + "\""
	};
	// FIXME: include options. 
// set terminal cairolatex
// {eps | pdf} done before. 
// {standalone | input}
// {blacktext | colortext | colourtext}
// {header <header> | noheader}
// {mono|color}
// {{no}transparent} {{no}crop} {background <rgbcolor>}
// {font <font>} {fontscale <scale>}
// {linewidth <lw>} {rounded|butt|square} {dashlength <dl>}
// {size <XX>{unit},<YY>{unit}}


//	if (update(pltFile, ptxFile)) {
	    log.debug("Running " + command + 
		      " -e...  on " + pltFile.getName() + ". ");
	    // may throw BuildExecutionException 
	    this.executor.execute(pltFile.getParentFile(), //workingDir 
				  this.settings.getTexPath(), //**** 
				  command, 
				  args);
//	}
	// no check: just warning that no output has been created. 
    }

    private void clearTargetPlt(File pltFile) 
	throws BuildExecutionException {

	this.log.info("Deleting targets of gnuplot-file " + pltFile + ". ");
	// may throw BuildExecutionException 
	this.fileUtils.replaceSuffix(pltFile, SUFFIX_PTX).delete();
	this.fileUtils.replaceSuffix(pltFile, SUFFIX_PDF).delete();
    }

    /**
     * Runs mpost on mp-files to generate mps-files. 
     *
     * @param mpFile
     *    the metapost file to be processed. 
     * @throws BuildExecutionException
     *    if running the mpost command failed. 
     * @see #processGraphics(File)
     */
    // used in processGraphics(Collection) only 
    private void runMetapost2mps(File mpFile) throws BuildExecutionException {

	this.log.info("Processing metapost-file " + mpFile + ". ");
	String command = this.settings.getMetapostCommand();
	File workingDir = mpFile.getParentFile();
	// for more information just type mpost --help 
	String[] args = buildArguments(this.settings.getMetapostOptions(), 
				       mpFile);
	log.debug("Running " + command + " on " + mpFile.getName() + ". ");
	// may throw BuildExecutionException 
	this.executor.execute(workingDir, 
			      this.settings.getTexPath(), //**** 
			      command, 
			      args);
	// from xxx.mp creates xxx1.mps, xxx.log and xxx.mpx 
	// FIXME: what is xxx.mpx for? 
	File logFile = this.fileUtils.replaceSuffix(mpFile, SUFFIX_LOG);
	logErrs(logFile, command, this.settings.getPatternErrMPost());
	// FIXME: what about warnings?
    }
    private void clearTargetMp(File mpFile) 
	throws BuildExecutionException {

	this.log.info("Deleting targets of metapost-file " + mpFile + ". ");
	// may throw BuildExecutionException 
	this.fileUtils.replaceSuffix(mpFile, SUFFIX_LOG).delete();
	this.fileUtils.replaceSuffix(mpFile, SUFFIX_FLS).delete();
	this.fileUtils.replaceSuffix(mpFile, SUFFIX_MPX).delete();
	// delete files xxxNumber.mps 
	String name1 = mpFile.getName();
	final String root = name1.substring(0, name1.lastIndexOf("."));
	FileFilter filter = new FileFilter() {
		public boolean accept(File file) {
		    return !file.isDirectory()
			&&  file.getName().matches(root + "\\d+" + SUFFIX_MPS);
		}
	    };

	// may throw BuildExecutionException 
	this.fileUtils.delete(mpFile, filter);
    }

    // FIXME: missing skip messages for svg 

    private void clearTargetSvg(File svgFile) 
	throws BuildExecutionException {

       this.log.info("Deleting targets of svg-file " + svgFile + ". ");
       // may throw BuildExecutionException 
       this.fileUtils.replaceSuffix(svgFile, SUFFIX_PDFTEX).delete();
//       this.fileUtils.replaceSuffix(svgFile, SUFFIX_PSTEX ).delete();
       this.fileUtils.replaceSuffix(svgFile, SUFFIX_PDF   ).delete();
   }

    private void addMainFile(File texFile) throws BuildExecutionException {
	// may throw BuildExecutionException
	if (this.fileUtils
	    .matchInFile(texFile, 
			 this.settings.getPatternLatexMainFile())) {
	    this.log.info("Detected latex-main-file " + texFile + ". ");
	    this.latexMainFiles.add(texFile);
	}
    }

    private void clearTargetTex(final File texFile) 
	throws BuildExecutionException {

	// may throw BuildExecutionException
	if (!this.fileUtils
	    .matchInFile(texFile, 
			 this.settings.getPatternLatexMainFile())) {
	    return;
	}

	// filter to delete 
	String name1 = texFile.getName();
	final String root = name1.substring(0, name1.lastIndexOf("."));
	FileFilter filter = new FileFilter() {
		public boolean accept(File file) {
		    return !file.isDirectory()
			&&  file.getName().startsWith(root)
			&& !file.equals(texFile);
		}
	    };
	this.log.info("Deleting files " + root + "... . ");
	// may throw BuildExecutionException 
	this.fileUtils.delete(texFile, filter);

	this.log.info("Deleting file zz" + root + ".eps. ");
	// FIXME: eliminate literal ".eps" 
	new File(texFile.getParent(), "zz" + root + ".eps").delete();
    }

    /**
     * Converts files in various graphic formats incompatible with LaTeX 
     * into formats which can be inputted or included directly 
     * into a latex file and returns the latex main files. 
     *
     * @throws BuildExecutionException
     */
    Collection<File> processGraphicsSelectMain(Collection<File> files) 
    	throws BuildExecutionException {

	this.latexMainFiles.clear();
	SuffixHandler handler;
	Collection<String> skipped = new TreeSet<String>();
	String suffix;
	for (File file : files) {
	    suffix = this.fileUtils.getSuffix(file);
	    handler = SUFFIX2HANDLER.get(suffix);
	    if (handler == null) {
		this.log.debug("Skipping processing of file " + file + ". ");
		skipped.add(suffix);
	    } else {
		// may throw BuildExecutionException 
		handler.transformSrc(file, this);
	    }
	}
	if (!skipped.isEmpty()) {
	    this.log.warn("Skipped processing of files with suffixes " + 
			  skipped + ". ");
	}
	return this.latexMainFiles;
    }

    /**
     * Deletes all created files. 
     */
    void clearCreated(Collection<File> files) throws BuildExecutionException {
	SuffixHandler handler;
	for (File file : files) {
	    handler = SUFFIX2HANDLER.get(this.fileUtils.getSuffix(file));
	    if (handler != null) {
		handler.clearTarget(file, this);
	    }
	}
   }
 
    // FIXME: suffix for tex files containing text and including pdf 
 }