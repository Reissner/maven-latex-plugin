package org.m2latex.core;

import java.io.File;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the contents of a directory. 
 *
 *
 * Created: Tue Dec  6 03:05:24 2016
 *
 * @author <a href="mailto:rei3ner@arcor.de">Ernst Reissner</a>
 * @version 1.0
 */
public class DirNode {

    // null iff this DirNode is invalid according to isValid() 
    /**
     * The set of regular files, i.e. files except directories 
     * in the directory described by this node. 
     * If the directory described by this node is not readable, 
     * this field is <code>null</code>. 
     *
     * @see #isValid()
     */
    private final Set<File> regularFiles;

    /**
     * The set of subdirectories 
     * in the directory described by this node: 
     * The keys are the names and the according values 
     * are the nodes describing the subdirectories. 
     * If the directory described by this node is not readable, 
     * this field is <code>null</code>. 
     *
     * @see #isValid()
     */
    private final Map<String, DirNode> name2node;

    /**
     * Creates a new <code>DirNode</code> instance.
     * <p>
     * Logging: 
     * WFU01 Cannot read directory 
     *
     * @param dir
     *    The directory this node represents 
     *    including subdirectories recursively. 
     *    This is the latex source directory or a subdirectory recursively. 
     * @param fileUtils
     *    
     */
    public DirNode(File dir, TexFileUtils fileUtils) {
	assert dir.isDirectory();
	// may log WFU01 Cannot read directory 
	File[] files = fileUtils.listFilesOrWarn(dir);
	if (files == null) {
	    // Here, this node is irregular 
	    this.regularFiles = null;
	    this.name2node = null;
	    return;
	}
	this.regularFiles = new TreeSet<File>();
	this.name2node = new TreeMap<String, DirNode>();
	DirNode node;
	for (File file : files) {
	    assert file.exists();
	    if (file.isDirectory()) {
		// may log WFU01 Cannot read directory 
		node = new DirNode(file, fileUtils);
		if (node.isValid()) {
		    this.name2node.put(file.getName(), node);
		}
	    } else {
		// FIXME: skip hidden files 
		this.regularFiles.add(file);
	    }
	}
    }

    /**
     * Whether the directory described by this node is readable. 
     */
    boolean isValid() {
	assert (this.regularFiles == null) == (this.name2node == null);
	return this.regularFiles != null;
    }

    Set<File> getRegularFiles() {
	return this.regularFiles;
    }

    Map<String, DirNode> getSubdirs() {
	return this.name2node;
    }
}
