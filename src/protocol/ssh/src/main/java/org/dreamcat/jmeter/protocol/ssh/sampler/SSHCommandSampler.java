/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dreamcat.jmeter.protocol.ssh.sampler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * SSH Sampler that collects single lines of output and returns
 * them as samples.
 *
 */
public class SSHCommandSampler extends AbstractSSHSampler {

    private static final long serialVersionUID = 234L;
    private static final Logger log = LoggerFactory.getLogger(SSHCommandSampler.class);

    private String command = "date";
    private boolean useReturnCode = true;
    private boolean useTty = true;
    private boolean printStdErr = true;

    public SSHCommandSampler() {
        super("SSH Command Sampler");
    }

    /**
     * Returns last line of output from the command
     */
    public SampleResult sample(Entry e) {
        SampleResult res = new SampleResult();
        res.setSampleLabel(getName() + ":(" + getUsername() + "@" + getHostname() + ":" + getPort() + ")");

        // Set up sampler return types
        res.setSamplerData(command);
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");

        String response;
        if (getSession() == null) {
            connect();
        }

        try {
            if (getSession() == null) {
                log.error("Failed to connect to server with credentials "
                        + getUsername() + "@" + getHostname() + ":" + getPort()
                        + " pw=" + getPassword());
                throw new NullPointerException("Failed to connect to server: " + getFailureReason());
            }

            response = doCommand(getSession(), command, res);
            res.setResponseData(response.getBytes());

            if(useReturnCode){
                res.setSuccessful("0".equals(res.getResponseCode()));
            }else{
                res.setSuccessful(true);
            }

            res.setResponseMessageOK();
        } catch (JSchException e1) {
            res.setSuccessful(false);
            res.setResponseCode("JSchException");
            res.setResponseMessage(e1.getMessage());
        } catch (IOException e1) {
            res.setSuccessful(false);
            res.setResponseCode("IOException");
            res.setResponseMessage(e1.getMessage());
        } catch (NullPointerException e1) {
            res.setSuccessful(false);
            res.setResponseCode("Connection Failed");
            res.setResponseMessage(e1.getMessage());
        }

        // Try a disconnect/sesson = null here instead of in finalize.
        disconnect();
        setSession(null);
        return res;
    }

    /**
     * Executes a the given command inside a short-lived channel in the session.
     *
     * Performance could be likely improved by reusing a single channel, though
     * the gains would be minimal compared to sharing the Session.
     *
     * @param session Session in which to create the channel
     * @param command Command to send to the server for execution
     * @return All standard output from the command
     * @throws JSchException
     * @throws IOException Error has occurred down in the network layer
     */
    private String doCommand(Session session, String command, SampleResult res) throws JSchException, IOException {
        StringBuilder sb = new StringBuilder();
        String[] Commands = command.trim().split("\n");


        res.sampleStart();
        for (int c = 0; c < Commands.length; c++) {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(channel.getErrStream()));
            channel.setPty(useTty);
            channel.setCommand(Commands[c]);
            channel.connect();

            if(printStdErr){
                sb.append("=== ").append(Commands[c]).append(" stdin ===\n");
            }

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }

            if(printStdErr){
                sb.append("\n\n=== stderr ===\n");
                for (String line = err.readLine(); line != null; line = err.readLine()) {
                    sb.append(line);
                    sb.append("\n");
                }
            }

            while (true) {
                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee){
                    //Do Nothing
                };
            }

            if(useReturnCode){
                res.setResponseCode(String.valueOf(channel.getExitStatus()));
            }else{
                res.setResponseCodeOK();
            }
            channel.disconnect();
            br.close();
            err.close();
        }
        res.sampleEnd();
        return sb.toString();
    }

    // Accessors
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean getPrintStdErr() {
        return printStdErr;
    }

    public void setPrintStdErr(boolean printStdErr) {
        this.printStdErr = printStdErr;
    }

    public boolean getUseReturnCode() {
        return useReturnCode;
    }

    public void setUseReturnCode(boolean useReturnCode) {
        this.useReturnCode = useReturnCode;
    }

    public boolean getUseTty() {
        return useTty;
    }

    public void setUseTty(boolean useTty) {
        this.useTty = useTty;
    }

}
