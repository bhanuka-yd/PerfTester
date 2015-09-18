package org.wso2.perftester.test;

import org.wso2.perftester.ssh.SSHConnection;

import java.io.IOException;

/**
 * Created by root on 9/17/15.
 */
public class Test004 {
    public static void main(String[] args) throws IOException {
        SSHConnection sshConnection = new SSHConnection("127.0.0.1","bhanuka","hello123");
        sshConnection.connect();
        System.out.println("Running");
        long timestart = System.currentTimeMillis();
        sshConnection.execCommand("sh waiter.sh");
        long timeend = System.currentTimeMillis();
        System.out.println("Running time = "+(timeend-timestart)/1000+"s");
    }
}
