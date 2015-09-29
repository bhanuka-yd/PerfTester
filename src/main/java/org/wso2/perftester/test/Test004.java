package org.wso2.perftester.test;

import org.wso2.perftester.ssh.SSHConnection;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 9/17/15.
 */
public class Test004 {
    public static void main(String[] args) throws IOException {
        File privateKey = new File("/home/bhanuka/.ssh/ebaytest.pem");
        SSHConnection sshConnection = new SSHConnection("54.172.250.119","ubuntu","");
        sshConnection.connectWithKeyFile(privateKey);
//        System.out.println("Running");
//        long timestart = System.currentTimeMillis();
//        sshConnection.execCommand("sh waiter.sh");
//        long timeend = System.currentTimeMillis();
//        System.out.println("Running time = "+(timeend-timestart)/1000+"s");
        sshConnection.execCommand("sudo touch helloWorld.txt");
    }
}
