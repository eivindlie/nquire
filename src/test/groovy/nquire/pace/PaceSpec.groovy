package nquire.pace

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class PaceSpec extends Specification {

    def setup() {
        feedbackList = new ArrayList<Feedback> ();

    }

    def cleanup() {
    }

    void "test 10 positives"() {
        when:
        for (int i = 0; i < 10; i++){
            feedbackList.push(
                    new Feedback(new Date(), true));

        }
        then:
        calculateCurrentPace(feedbackList) > 50;
        

    }


}