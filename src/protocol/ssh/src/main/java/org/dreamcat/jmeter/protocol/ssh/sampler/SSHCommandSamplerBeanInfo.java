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

import org.apache.jmeter.testbeans.gui.TextAreaEditor;

public class SSHCommandSamplerBeanInfo extends AbstractSSHBeanInfoSupport {


    public SSHCommandSamplerBeanInfo() {

        super(SSHCommandSampler.class);

        PropertyDescriptor p = property("command"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setPropertyEditorClass(TextAreaEditor.class);
        p.setValue(TEXT_LANGUAGE, "shell");  // $NON-NLS-1$

        p = property("useReturnCode"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);

        p = property("printStdErr"); // $NON-NLS-1$
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.FALSE);

        createPropertyGroup("execute", new String[]{
                    "command", // $NON-NLS-1$
                    "useReturnCode", // $NON-NLS-1$
                    "printStdErr" // $NON-NLS-1$
                });

    }

}
