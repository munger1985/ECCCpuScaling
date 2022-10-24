package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RunCmd {
    private static final Logger log = LoggerFactory.getLogger(RunCmd.class);

    String cmd = "date";
    Process p = null;
    String[] param =new String[3];

    public RunCmd(String cmd) {
        this.cmd = cmd;
    }

    String run() {

        try {
            List<String> commandList = new ArrayList<>();
            commandList.add("/bin/bash");
//            commandList.add("-c");
            cmd = "./" + cmd;
            commandList.add(cmd);
            for (int i=0;i<param.length ;i++ ) {
                if(param[i]!=null){
//                    System.out.println("param"+param[i]);
                    commandList.add(param[i]);
                }
            }
            // 起子进程执行cmd命令
            ProcessBuilder pb = new ProcessBuilder(commandList);
            p = pb.start();
            // 等待命令执行结束
            int exitValue = p.waitFor();
            // 创建readers， resReader用于读取标准输出，errReader用于读取错误输出
            BufferedReader resReader = new BufferedReader(new InputStreamReader((p.getInputStream())));
            BufferedReader errReader = new BufferedReader(new InputStreamReader((p.getErrorStream())));

            StringBuilder resStringBuilder = new StringBuilder();
            StringBuilder errStringBuilder = new StringBuilder();
            String line;
            while ((line = resReader.readLine()) != null) {
                resStringBuilder.append(line);
            }
            while ((line = errReader.readLine()) != null) {
                errStringBuilder.append(line);
            }

            // linux标准， exitValue为0时，表示执行正确结束
            // 当exitValue > 0时，抛出异常，并将获取的错误信息包装在Exception中
            if (exitValue > 0) {
                log.error(errStringBuilder.toString());
                throw new RuntimeException(errStringBuilder.toString());
            }


            // 返回标准输出
            return resStringBuilder.toString();
        } catch (Exception e) {
            log.error(e.getMessage() );


        }
        return "did not run";
    }

}
