package org.wso2.perftester.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 9/17/15.
 */
public class Test003 {
    public static void main(String[] args) {
        /*for(String x:getValuesWithinCurlyBrackets("{startTransport netty-gw}{5000}")){
            System.out.println(x);
        }*/
        String testLine ="RUN:-echo $JAVA_HOME >/home/boauser/helloMister.txt <PROMPT> {startTransport netty-gw}{5000}";
        System.out.println(testLine.split("<PROMPT>").length);
        /*if(matcher.find()){
            System.out.println(matcher.group(0));
            matcher.find();
            System.out.println(matcher.group(1));
        }*/
    }

    private static ArrayList<String>getValuesWithinCurlyBrackets(String line){
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(line);
        ArrayList<String> matches = new ArrayList<String>();
        while(matcher.find()){
            String match = matcher.group();
            match=match.substring(1,match.length()-1);
            matches.add(match.trim());
        }
        return matches;
    }
}
