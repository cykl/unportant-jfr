package info.unportant.jfr

import spock.lang.Requires
import spock.lang.Specification

import java.nio.file.Paths

@Requires({ JreVersion.isOracle() })
class JavaFlightRecordingConfSpec extends Specification {

    def "When not set, a random name is picked"() {
        given:
        def recording = JavaFlightRecording.builder().build();

        expect:
        recording.getConf().getName() != null
    }

    def "When not set, the output file is named using the name attribute and stored in CWD"() {
        given:
        def recording = JavaFlightRecording.builder().build();

        expect:
        def conf = recording.conf
        conf.outputPath == Paths.get("${conf.name}.jfr")
    }

    def "When not set, the output file is not compressed"() {
        given:
        def recording = JavaFlightRecording.builder().build();

        expect:
        def conf = recording.conf
        conf.enableCompression() == false
    }
}
