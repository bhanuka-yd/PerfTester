package org.wso2.perftester.test;

import org.wso2.perftester.bootstrap.Bootstrap;

/**
 * Created by root on 9/15/15.
 */
public class Test002 {
    public static void main(String[] args) throws Exception {
        Bootstrap.main(new String[]{"touch @@.txt %hello1",
                "/home/bhanuka/MSS/PerfTesterJava/PerfTester/configs/perfconfig.txt", });
    }
}
