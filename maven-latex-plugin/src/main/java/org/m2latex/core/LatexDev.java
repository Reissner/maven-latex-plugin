package org.m2latex.core;

import java.io.File;

/**
 * Enumeration of the backends of latex. 
 *
 *
 * Created: Tue Oct 18 10:06:30 2016
 *
 * @author <a href="mailto:rei3ner@arcor.de">Ernst Reissner</a>
 * @version 1.0
 */
public enum LatexDev {

    // lualatex creates pdf 
    pdf {
	String  getXFigTexLanguage() {
	    return "pdftex_t";
	}
	String  getXFigInTexLanguage() {
	    return "pdftex";
	}
	String getGnuplotInTexLanguage() {
	    return "pdf";
	}
	String getXFigInTexFile(TexFileUtils fileUtils, File srcFile) {
	    return fileUtils.replaceSuffix(srcFile, "pdf").toString();
	}
    },
    // latex creates dvi but not with the given drivers. 
    dvips {
	String  getXFigTexLanguage() {
	    return "pstex_t";
	}
	String  getXFigInTexLanguage() {
	    return "pstex";
	}
	String getGnuplotInTexLanguage() {
	    return "eps";
	}
	String getXFigInTexFile(TexFileUtils fileUtils, File srcFile) {
	    return fileUtils.replaceSuffix(srcFile, "pstex").toString();
	}
    };

    /**
     * Returns the name of the language <code>fig2dev</code> uses 
     * to convert the text of an xfig-picture into. 
     * In fact, this is latex-code with file in graphic format 
     * embedded with <code>\includegraphics</code>. 
     * The language code of the embedded graphic file 
     * is given by {@link #getXFigInTexLanguage()}. 
     */
    abstract String getXFigTexLanguage();


    /**
     * Returns the name of the language <code>fig2dev</code> uses 
     * to convert the graphic without text of an xfig-picture into. 
     * In fact, there is a file of that format 
     * embedded with <code>\includegraphics</code> in latex-code 
     * representing text. 
     * The language code of the text file enclosing the graphic 
     * is given by {@link #getXFigTexLanguage()}. 
     */
    abstract String getXFigInTexLanguage();

    /**
     * Returns the name of the language <code>gnuplot</code> uses 
     * to convert the graphic without text of a gnuplot-picture into. 
     * In fact, there is a file of that format 
     * embedded with <code>\includegraphics</code> in latex-code 
     * representing text. 
     */
    abstract String getGnuplotInTexLanguage();

    /**
     * Returns the name of the language <code>gnuplot</code> uses 
     * to convert the graphic without text of a gnuplot-picture into. 
     * In fact, there is a file of that format 
     * embedded with <code>\includegraphics</code> in latex-code 
     * representing text. 
     */
    abstract String getXFigInTexFile(TexFileUtils fileUtils, File srcFile);
}