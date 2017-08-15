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

package info.unportant.jfr

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Requires
import spock.lang.Specification

class JavaFlightRecordingSpec extends Specification {

    @Rule
    TemporaryFolder tmpFolder = new TemporaryFolder()

    @Requires({ JreVersion.isOracle() })
    def "The recording file must be created at the specified location"() {
        given:
        def recordingFile = tmpFolder.newFile().toPath()
        def recording = JavaFlightRecording.builder()
                .withOutputPath(recordingFile)
                .build();

        when:
        recording.start();
        recording.stop();

        then:
        recordingFile.size() > 0
    }

    @Requires({ JreVersion.isOracle() })
    def "When the output file cannot be written, stop should raise and exception"() {
        given:
        def recording = JavaFlightRecording.builder()
                .withOutputPath("/i/cannot/write/here.jfr")
                .build();

        recording.start();

        when:
        recording.stop();

        then:
        JfrRecordingError e = thrown()
        e.message.contains("Could not write recording")
    }

    @Requires({ JreVersion.isOpenJDK() })
    def "When using OpenJDK, an exception explaining that JFR is not available is thrown"() {
        when:
        JavaFlightRecording.builder().build();

        then:
        JfrNotAvailable e = thrown()
        e.message.contains("OpenJDK")
    }
}
