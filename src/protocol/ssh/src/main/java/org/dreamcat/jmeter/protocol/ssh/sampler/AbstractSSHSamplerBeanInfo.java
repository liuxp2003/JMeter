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

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;

public abstract class AbstractSSHSamplerBeanInfo extends BeanInfoSupport {

    public AbstractSSHSamplerBeanInfo(Class<? extends AbstractSSHSampler> clazz) {
        super(clazz);

        createPropertyGroup("server", // $NON-NLS-1$
                new String[]{
                    "hostname", // $NON-NLS-1$
                    "port", // $NON-NLS-1$
                    "connectionTimeout"
                });

        createPropertyGroup("user", // $NON-NLS-1$
                new String[]{
                    "username", // $NON-NLS-1$
                    "password" // $NON-NLS-1$
                });

        createPropertyGroup("keyFile",
                new String[]{
                    "sshkeyfile", // $NON-NLS-1$
                    "passphrase" // $NON-NLS-1$
                });

        PropertyDescriptor p;
        p = property("username"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("password", TypeEditor.PasswordEditor); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("sshkeyfile");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setPropertyEditorClass(FileEditor.class);

        p = property("passphrase");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("hostname"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("port"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, 22);

        p = property("connectionTimeout");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, 5000);

    }
}
