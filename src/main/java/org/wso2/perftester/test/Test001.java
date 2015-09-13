package org.wso2.perftester.test;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.wso2.perftester.abstractionLayer.LoadTest;
import org.wso2.perftester.bashTest.BashTest;
import org.wso2.perftester.bootstrap.Bootstrap;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by root on 9/11/15.
 */
public class Test001 {
    public static void main(String[] args) throws Exception {

        Bootstrap.main(new String[]{"sudo touch @@-@@.txt % hey,youngster % helloworld,master"});

    }
}
