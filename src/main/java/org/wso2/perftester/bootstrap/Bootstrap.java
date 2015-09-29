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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 9/11/15.
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {

        if(args.length==0){
            throw new IllegalArgumentException("No Commandline arguements found");
        }

        File configFile;
        if(args[1]!=null){
            configFile = new File(args[1]);
            if(!configFile.exists()){
                throw new IllegalArgumentException("Config File does not exist");
            }
        }else{
            throw new IllegalArgumentException("Please specify a config file");
        }

        ArrayList<ArrayList<HashMap<String,String>>> configs = new ArrayList<ArrayList<HashMap<String,String>>>();
        makeConfigMap(configs,configFile);

        String [] arguements = args[0].split("%");

        String command = arguements[0];

        if(arguements.length-1!=configs.size()){
            throw new Exception("Number of configurations on the command line doesn't match with the number"
                    + "of configurations in the config file");
        }
        System.out.println("\n\n-----------------------------------------------------------------------------\n");
        for(int x=1;x<arguements.length;x++){
            String tempCommand = command;

            for(String y:arguements[x].split(",")){
                y=y.trim();
                tempCommand = tempCommand.replaceFirst("@@",y);
            }

            for(HashMap<String,String> currentConfigs : configs.get(x-1)){
                runRemoteCommands(currentConfigs);
            }

            LoadTest loadTest = new BashTest();
            System.out.println("Running Command - "+tempCommand + "\n");
            loadTest.runTest(tempCommand);
        }
        System.out.println("All Tests Completed");
        System.out.println("\n-----------------------------------------------------------------------------\n\n");

    }

    private static void makeConfigMap(ArrayList<ArrayList<HashMap<String,String>>> configs,File configFile) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(configFile));
        String line = null;
        ArrayList<HashMap<String,String>> currentList = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> currentMap=new HashMap<String,String>();
        currentList.add(currentMap);
        configs.add(currentList);
        while((line=fileReader.readLine())!=null){
            String [] keyVal= line.split(":-");

            switch (keyVal[0]){
            case "----":{
                if(currentList.size()==0){
                    currentList.add(currentMap);
                }
                currentMap = new HashMap<String,String>();
                currentList.add(currentMap);
                break;
            }
            case "##$$##":{

                if(configs.size()==0){
                    if(currentList.size()==0){
                        currentList.add(currentMap);
                    }
                    configs.add(currentList);
                }
                currentList = new ArrayList<HashMap<String,String>>();
                currentMap = new HashMap<String,String>();
                currentList.add(currentMap);
                configs.add(currentList);
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
                currentMap.put("password",(keyVal.length>1)?keyVal[1]:"");
                break;
            }case "keyfile": {
                if(keyVal.length>2){
                    throw new IllegalArgumentException("Key File value cannot be found");
                }
                currentMap.put("keyfile",keyVal[1]);
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
            case "WAIT":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("WAIT value cannot be found");
                }
                if(!isNumeric(keyVal[1].trim())){
                    throw new IllegalArgumentException("Invalid WAIT value");
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
            case "env":{
                if(keyVal.length<2){
                    throw new IllegalArgumentException("env Filepath not found");
                }
                File f = new File(keyVal[1].trim());
                if(!f.exists()){
                    throw new FileNotFoundException("env file not found");
                }
                currentMap.put("env",keyVal[1].trim());
                break;
            }
            }
        }
    }

    private static void runRemoteCommands(HashMap<String,String> currentConfigs) throws IOException {

        SSHConnection conn = new SSHConnection(currentConfigs.get("host"),
                currentConfigs.get("username"),
                currentConfigs.get("password"));


        if(currentConfigs.containsKey("keyfile")){
            File privateKey= new File(currentConfigs.get("keyfile"));
            if(!privateKey.exists()){
                throw new FileNotFoundException("Private Key cannot be found");
            }
            conn.connectWithKeyFile(privateKey);

        }else {
            conn.connect();
        }

        String premadeEnv="";

        if(currentConfigs.get("env")!=null) {
            File envFile = new File(currentConfigs.get("env"));
            BufferedReader envReader = new BufferedReader(new FileReader(envFile));
            String enVariable = null;
            while ((enVariable = envReader.readLine()) != null) {
                String[] trimmer = enVariable.split("=");
                if (trimmer.length < 2) {
                    continue;
                }
                enVariable = trimmer[0].trim() + "=" + trimmer[1].trim();
                premadeEnv = enVariable + " && ";
            }
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
            if(sshCommandSplit[0].trim().equals("RUN")){
                String tempCommand = premadeEnv+sshCommandSplit[1].trim();


                if(sshCommandSplit[1].trim().split("<PROMPT>").length>1){

                    String promptString = sshCommandSplit[1].trim().split("<PROMPT>")[1].trim();
                    ArrayList<String> vals = getValuesWithinCurlyBrackets(promptString);
                    if((vals.size()>2)&&isNumeric(vals.get(1))&&isNumeric(vals.get(2))){

                        System.out.println("Running Remote Command - " + sshCommandSplit[1].trim().split("<PROMPT>")[0].trim() + " With Prompt "+vals.get(0).trim());
                        conn.execCommandWithPrompt(premadeEnv+sshCommandSplit[1].trim().split("<PROMPT>")[0].trim(),
                                vals.get(0).trim(),Long.parseLong(vals.get(1)),Long.parseLong(vals.get(2)));

                    }else{
                        System.err.println("Ignoring Command Invalid Variables\n");
                    }
                }else{
                    System.out.println("Running Remote Command - " + tempCommand + "\n");
                    conn.execCommand(tempCommand);
                }
            }else if(sshCommandSplit[0].trim().equals("UPLOAD")){
                String [] fileUpload = sshCommandSplit[1].trim().split("-->>");
                if(fileUpload.length<2){
                    continue;
                }

                File f = new File(fileUpload[0].trim());
                if(!f.exists()){
                    continue;
                }
                System.out.println("Uploading file - " + f.getName()+"\n");
                conn.putFile(f,fileUpload[1].trim());
            }else if(sshCommandSplit[0].trim().equals("WAIT")){
                Long waitTime = Long.parseLong(sshCommandSplit[1].trim());
                System.out.println("Waiting the Thread for "+waitTime+" millisecond(s)\n");
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static boolean isNumeric(String value){
        try{
         long val = Long.parseLong(value);
        }
        catch (Exception e){
            return false;
        }
        return true;
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
