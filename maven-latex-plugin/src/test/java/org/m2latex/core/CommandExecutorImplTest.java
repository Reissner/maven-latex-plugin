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

import org.m2latex.mojo.MavenLogWrapper;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.SystemStreamLog;

import org.easymock.MockControl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;

public class CommandExecutorImplTest {
    private final static String WORKING_DIR = 
	System.getProperty("testResourcesDir");

    private MockControl executorCtrl = MockControl
	.createStrictControl(CommandExecutor.class);

    private CommandExecutor executor = (CommandExecutor) executorCtrl.getMock();

    private static void cleanWorkingDir() {
	File wDir = new File(WORKING_DIR);
	assert wDir.isDirectory() : "Expected directory. ";
	File[] files = wDir.listFiles();
	assert files != null : "Working directory is not readable. ";
	for (File file : files) {
	    file.delete();
	}
    }

    @Before public void setUp() throws IOException {
	cleanWorkingDir();
    }

    @Test public void testExecute() throws BuildFailureException {
        CommandExecutorImpl executor = 
	    new CommandExecutorImpl(new MavenLogWrapper(new SystemStreamLog()));
 	File workingDir = new File(WORKING_DIR);
        String touchFile = "cmdLineExe.touch";
	String output = executor.execute(workingDir, 
					 null, 
					 "touch", 
					 new String[] {touchFile},
					 new File(workingDir, touchFile));
        //assertEquals(echoText, output.subSequence(0, echoText.length()));
    }

    // void mockExecute() throws BuildFailureException {
    // 	File res = new File("/tmp/exists.latexPlugin");
    // 	res.delete();
    //     this.executor.execute(new File("/tmp/"),
    // 			      null,
    // 			      "touch",
    // 			      new String[] {res.getName()},
    // 			      res);
    //     executorCtrl.setMatcher(MockControl.ARRAY_MATCHER);
    //     executorCtrl.setReturnValue(null);
    // }
}
