package org.wso2.perftester.bootstrap;

import org.wso2.perftester.abstractionLayer.LoadTest;
import org.wso2.perftester.bashTest.BashTest;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by root on 9/11/15.
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {

        String [] arguements = args[0].split("%");

        String command = arguements[0];


        for(int x=1;x<arguements.length;x++){
            String tempCommand = command;

            for(String y:arguements[x].split(",")){
                y=y.trim();
                tempCommand = tempCommand.replaceFirst("\\@\\@",y);
            }

            LoadTest loadTest = new BashTest();
            loadTest.runTest(tempCommand);
        }
    }
}
