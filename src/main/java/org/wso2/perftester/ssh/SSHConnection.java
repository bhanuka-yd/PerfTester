package org.wso2.perftester.ssh;

import ch.ethz.ssh2.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by root on 9/15/15.
 */
public class SSHConnection {
    private String host,username,password;
    private Connection conn;

    public SSHConnection(String host,String username,String password){
        this.host=host;
        this.username=username;
        this.password=password;
        conn=new Connection(host);
    }

    public ConnectionInfo connect() throws IOException {
        ConnectionInfo info = conn.connect();
        boolean isAuthenticated = conn.authenticateWithPassword(username,password);
        if(!isAuthenticated){
            throw new IOException("Authentication Failed");
        }
        return info;
    }

    public void execCommand(String command) throws IOException {
        Session session = conn.openSession();
        session.execCommand(command);

        int b = 1;
        InputStream is = new StreamGobbler(session.getStdout());
        while ((b = is.read()) != -1) {
            System.out.print((char)b);
        }
        session.close();
    }

    public void putFile(File f,String remotePath) throws IOException {
        SCPClient fileClient = new SCPClient(conn);
        SCPOutputStream outputStream =fileClient.put(f.getName(), f.length(), remotePath, "0755");
        FileInputStream inputStream = new FileInputStream(f);
        IOUtils.copy(inputStream,outputStream);
        inputStream.close();
        outputStream.close();
    }

    public void closeConnection(){
        conn.close();
    }
}
