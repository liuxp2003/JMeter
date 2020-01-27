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

import java.io.Serializable;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.testbeans.TestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Abstract SSH Sampler that manage SSH connexion and delegates sampling.
 */
public abstract class AbstractSSHSampler extends AbstractSampler implements TestBean {

    private static final long serialVersionUID = 234L;
    private static final Logger log = LoggerFactory.getLogger(AbstractSSHSampler.class);

    private String hostname = "";

    private int port = 22;

    private String username = "";

    private String password = "";

    private int connectionTimeout = 5000;

    private String failureReason = "Unknown";
    private static final JSch jsch = new JSch();

    private Session session = null;

    private SSHSamplerUserInfo userinfo = null;

    public AbstractSSHSampler(String name) {
        super();
        setName(name);
        userinfo = new SSHSamplerUserInfo(this);
    }

    /**
     * Sets up SSH Session on connection start
     */
    public void connect() {
        try {
            failureReason = "Unknown";
            session = jsch.getSession(getUsername(), getHostname(), getPort());
            // session.setPassword(getPassword()); // Use a userinfo instead
            session.setUserInfo(userinfo);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("kex", "diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha256,ecdh-sha2-nistp256,ecdh-sha2-nistp521");
            session.connect(connectionTimeout);
        } catch (JSchException e) {
            failureReason = e.getMessage();
            session.disconnect();
            session = null;
            log.error("SSH connexion error", e);
        }
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }


    public String getHostname() {
        return hostname;
    }


    public void setHostname(String server) {
        this.hostname = server;
    }


    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public int getConnectionTimeout() {
        return connectionTimeout;
    }


    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    protected Session getSession() {
        return session;
    }


    protected void setSession(Session session) {
        this.session = session;
    }


    protected String getFailureReason() {
        return failureReason;
    }


    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable e) {
            log.error("SSH finalize error", e);
        } finally {
            if (session != null) {
                session.disconnect();
                session = null;
            }
        }
    }

    /**
     * A private implementation of com.jcraft.jsch.UserInfo.
     * This takes a AbstractSSHSampler when constructed and looks over its data when queried for information.
     * This should only be visible to the SSH Sampler class.
     */
    private class SSHSamplerUserInfo implements UserInfo, Serializable {

        private static final long serialVersionUID = 234L;

        private AbstractSSHSampler owner;

        public SSHSamplerUserInfo(AbstractSSHSampler owner) {
            this.owner = owner;
        }

        public String getPassphrase() {
            return null;
        }

        public String getPassword() {
            String retval = owner.getPassword();
            if (retval.length() == 0) {
                retval = null;
            }
            return retval;
        }

        /* Prompts/show should be taken care of by Jmeter */
        public boolean promptPassword(String message) {
            return true;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public void showMessage(String message) {
            return;
        }

    }

}
