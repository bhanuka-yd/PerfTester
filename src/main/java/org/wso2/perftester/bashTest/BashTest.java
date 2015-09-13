package org.wso2.perftester.bashTest;

import org.wso2.perftester.abstractionLayer.LoadTest;

import java.io.*;

/**
 * Created by root on 9/11/15.
 */
public class BashTest implements LoadTest{

    public void runTest(String command) throws Exception {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);

    }
}