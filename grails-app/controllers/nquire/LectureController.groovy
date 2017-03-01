package nquire

import grails.plugin.springsecurity.annotation.Secured
import nquire.websocket.LectureEndpoint
import nquire.websocket.LectureHandler

import java.security.SecureRandom

@Secured('ROLE_LECTURER')
class LectureController {

    static allowedMethods = [present: 'GET', create_lecture: 'POST'];

    def index() {

    }

    def present() {
        render(view: 'present')
    }

    @Secured('permitAll')
    def connect() {
        if(params.id == null) {
            // No lecture id given
            redirect("/")
            // TODO Redirect to form for lecture connection
        }

        int id = params.int('id')

        if(!LectureEndpoint.isAlive(id)) {
            // Lecture does not exist
            // TODO Handle this...
        }

        render(view: 'student', model: [lectureId: id])
    }

    def create_lecture() {
        if(principal.currentLecture != 0) {
            // TODO Send message that a lecture is already running
            return
        }

        int id = 0
        String token = new BigInteger(130, new SecureRandom()).toString()

        boolean isAdded = false
        while(!isAdded) {
            id = new Random().nextInt(8999) + 1000
            isAdded = LectureEndpoint.addLecture(new LectureHandler(id, token), id)
        }

        principal.currentLecture = id
        principal.lectureToken = token

        redirect(action: 'present')
    }
}
