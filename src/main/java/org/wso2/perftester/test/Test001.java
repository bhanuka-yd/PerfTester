package org.wso2.perftester.test;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.wso2.perftester.abstractionLayer.LoadTest;
import org.wso2.perftester.bashTest.BashTest;
import org.wso2.perftester.bootstrap.Bootstrap;
import org.wso2.perftester.ssh.SSHConnection;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by root on 9/11/15.
 */
public class Test001 {
    public static void main(String[] args) throws Exception {

//        Bootstrap.main(new String[]{"sudo touch @@-@@.txt % hey,youngster % helloworld,master"});

//        runTest("sudo touch hello.txt");
//        runTest("sudo chown bhanuka.bhanuka hello.txt");
//        runTest("sudo scp hello.txt boauser@204.13.85.5:/home/boauser\n");
        SSHConnection connection = new SSHConnection("204.13.85.5","boauser","boa@123");
        connection.connect();
        File f = new File("/home/bhanuka/MSS/configs/helloDoc1");
        connection.putFile(f,"/home/boauser");
//        connection.execCommand(". .bashrc");
//        connection.execCommand(". /etc/profile");
//        connection.execCommand(". /etc/environment");
//        connection.execCommand("export JAVA_HOME=/usr/local/java/jdk1.7.0_67 && echo $JAVA_HOME");
//        connection.execCommand("export PATH=$PATH:$JAVA_HOME/bin");
//        connection.execCommand("echo $JAVA_HOME");

//        connection.execCommand("export JAVA_HOME=/usr/local/java/jdk1.7.0_67 && sh "
//                + "/home/boauser/wso2gw/perf/test/wso2esb-4.9.0-BETA-SNAPSHOT/bin/wso2server.sh stop");

//        connection.execCommand("echo testing line 2 >/home/boauser/hello.txt");
//        connection.execCommand(">/home/boauser/hello.txt");
        connection.execCommand("export vice=vice1 && export nice=nice1 && export rice=rice && echo $vice && echo $nice && echo $rice");
        connection.closeConnection();


    }
    public static void runTest(String command) throws Exception {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line= null;
        while((line =reader.readLine())!=null){
            System.out.println();
        }
    }
}
