package org.wso2.perftester.bootstrap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.perftester.abstractionLayer.LoadTest;
import org.wso2.perftester.bashTest.BashTest;
import org.wso2.perftester.ssh.SSHConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by root on 9/11/15.
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {

        File configFile;
        if(args[1]!=null){
            configFile = new File(args[1]);
            if(!configFile.exists()){
                throw new IllegalArgumentException("Config File does not exist");
            }
        }else{
            throw new IllegalArgumentException("Please specify a config file");
        }
        File envs;
        if(args[2]!=null){
            envs = new File(args[2]);
            if(!configFile.exists()){
                throw new IllegalArgumentException("Environment variables File does not exist");
            }
        }else{
            throw new IllegalArgumentException("Please specify a Environment variables file");
        }


        ArrayList<HashMap<String,String>> configs = new ArrayList<HashMap<String,String>>();
        makeConfigMap(configs,configFile);

        String [] arguements = args[0].split("%");

        String command = arguements[0];

        if(arguements.length-1!=configs.size()){
            throw new Exception("Number of configurations on the command line doesn't match with the number"
                    + "of configurations in the config file");
        }

        for(int x=1;x<arguements.length;x++){
            String tempCommand = command;

            for(String y:arguements[x].split(",")){
                y=y.trim();
                tempCommand = tempCommand.replaceFirst("@@",y);
            }

            HashMap<String,String> currentConfigs = configs.get(x-1);
            runRemoteCommands(currentConfigs,envs);
            LoadTest loadTest = new BashTest();
            System.out.println("Running Command - "+tempCommand);
            loadTest.runTest(tempCommand);
        }
        System.out.println("All Tests Completed");
    }

    private static void makeConfigMap(ArrayList<HashMap<String,String>> configs,File configFile) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(configFile));
        String line = null;
        HashMap<String,String> currentMap=new HashMap<String,String>();
        while((line=fileReader.readLine())!=null){
            String [] keyVal= line.split(":-");

            switch (keyVal[0]){
            case "##$$##":{

                if(configs.size()==0){
                    configs.add(currentMap);
                }
                currentMap = new HashMap<String,String>();
                configs.add(currentMap);
                break;
            }
            case "host":{
                if(keyVal.length<2){
                    System.out.println(Arrays.toString(keyVal));
                    throw new IllegalArgumentException("Host value cannot be found");
                }
                currentMap.put("host",keyVal[1]);
                break;
            }
            case "username":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("Username value cannot be found");
                }
                currentMap.put("username",keyVal[1]);
                break;
            }
            case "password":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("Password value cannot be found");
                }
                currentMap.put("password",keyVal[1]);
                break;
            }
            case "RUN":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("RUN command cannot be found");
                }
                JSONArray arr =null;
                if(currentMap.get("COMMAND")==null){
                    arr = new JSONArray();
                }else{
                    arr = new JSONArray(currentMap.get("COMMAND"));
                }

                arr.put(line);
                currentMap.put("COMMAND",arr.toString());
                break;
            }
            case "UPLOAD":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("UPLOAD command cannot be found");
                }
                JSONArray arr =null;
                if(currentMap.get("COMMAND")==null){
                    arr = new JSONArray();
                }else{
                    arr = new JSONArray(currentMap.get("COMMAND"));
                }

                arr.put(line);
                currentMap.put("COMMAND",arr.toString());
                break;
            }
            }
        }
    }

    private static void runRemoteCommands(HashMap<String,String> currentConfigs,File envs) throws IOException {
        SSHConnection conn = new SSHConnection(currentConfigs.get("host"),
                currentConfigs.get("username"),
                currentConfigs.get("password"));

        conn.connect();

        BufferedReader envReader = new BufferedReader(new FileReader(envs));
        String premadeEnv="";
        String enVariable=null;
        while((enVariable=envReader.readLine())!=null){
            String [] trimmer = enVariable.split("=");
            if(trimmer.length<2){
                continue;
            }
            enVariable = trimmer[0].trim()+"="+trimmer[1].trim();
            premadeEnv=enVariable+" && ";
        }

        JSONArray commandArray;
        if(currentConfigs.get("COMMAND")!=null){
            commandArray=new JSONArray(currentConfigs.get("COMMAND"));
        }else{
            commandArray = new JSONArray();
        }

        for(int confCount=0;confCount<commandArray.length();confCount++){
            String sshCommand = commandArray.getString(confCount);
            String [] sshCommandSplit = sshCommand.split(":-");
            if(sshCommandSplit.length<2){
                continue;
            }
            if(sshCommandSplit[0].equals("RUN")){
                String tempCommand = premadeEnv+sshCommandSplit[1];
                System.out.println("Running Remote Command - "+tempCommand);
                conn.execCommand(tempCommand);
            }else if(sshCommandSplit[0].equals("UPLOAD")){
                String [] fileUpload = sshCommandSplit[1].split("-->>");
                if(fileUpload.length<2){
                    continue;
                }

                File f = new File(fileUpload[0].trim());
                if(!f.exists()){
                    continue;
                }
                System.out.println("Uploading file - " + f.getName());
                conn.putFile(f,fileUpload[1].trim());
            }
        }
    }
}
