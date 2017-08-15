/*
 * Copyright (c) 2015-2017, Clément MATHIEU
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package info.unportant.jfr;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Basic API to interact with JFR using the DiagnosticCommand MBean.
 *
 * @See com.sun.management.DiagnosticCommandMBean
 */
class DiagnosticCommand {

    private final MBeanServer mBeanServer;
    private final ObjectName objectName;

    DiagnosticCommand() throws DiagnosticCommandBeanNotAvailable {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.objectName = getObjectName();
    }

    private ObjectName getObjectName() throws DiagnosticCommandBeanNotAvailable {
        try {
            return new ObjectName("com.sun.management:type=DiagnosticCommand");
        } catch (MalformedObjectNameException e) {
            throw new DiagnosticCommandBeanNotAvailable();
        }
    }

    // mBeanServer's exceptions are converted into undocumented runtime exceptions.
    // Since the JVM version has been checked earlier, we know that theses operations won't fail.

    public void unlockCommercialFeatures() {
        try {
            mBeanServer.invoke(objectName, "vmUnlockCommercialFeatures", null, null);
        } catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
            throw new JfrRecordingError(e);
        }
    }

    public void jfrStart(String... args) {
        try {
            Object[] argsObj = new Object[]{args};
            mBeanServer.invoke(objectName, "jfrStart", argsObj, StringArraySignature());
        } catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
            throw new JfrRecordingError(e);
        }
    }

    public String jfrStop(String... args) {
        try {
            Object[] argsObj = new Object[]{args};
            Object ret = mBeanServer.invoke(objectName, "jfrStop", argsObj, StringArraySignature());
            return (String) ret;
        } catch (InstanceNotFoundException | MBeanException | ReflectionException e) {
            throw new JfrRecordingError(e);
        }
    }

    private String[] StringArraySignature() {
        return new String[]{String[].class.getName()};
    }
}
