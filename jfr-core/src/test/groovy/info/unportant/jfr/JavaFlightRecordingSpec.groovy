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
