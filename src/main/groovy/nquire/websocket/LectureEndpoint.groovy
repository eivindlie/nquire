package nquire.websocket

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.CloseStatus

class LectureEndpoint implements WebSocketHandler {

    private static Map<Integer, LectureHandler> lectures = new HashMap<>()
    private static Map<WebSocketSession, LectureHandler> lecturesByUser = new HashMap<>()
    private static List<WebSocketSession> unassignedUsers = new ArrayList<>()

    static Log log = LogFactory.getLog(getClass())

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        unassignedUsers.add(session);
    }

    @Override
    void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String msg = message?.payload;

        if(unassignedUsers.contains(session)) {
            // User is not assigned to a lecture - check if this is a connect message
            def mObject = new JsonSlurper().parseText(msg)

            if(mObject.type == "connect") {
                if(!lectures.containsKey(mObject.lectureId)) {
                    // Lecture does not exist
                    String errorMessage = JsonOutput.toJson([type   : 'error',
                                                    code   : 404,
                                                    message: 'No lecture with the provided ID exists!'])
                    session.sendMessage(new TextMessage(errorMessage))
                    return
                }

                if(mObject.role == "student") {
                    lectures.get(mObject.lectureId).addStudent(session)
                    lecturesByUser.put(session, lectures.get(mObject.lectureId))
                    unassignedUsers.remove(session)
                } else if(mObject.role == "lecturer") {
                    if(!lectures.get(mObject.lectureId).addLecturer(session, mObject.token)) {
                        String errorMsg = JsonOutput.toJson([type: 'error',
                                                        code: 403,
                                                        message: 'The provided token did not authenticate against the lecture.'])
                        session.sendMessage(new TextMessage(errorMsg))
                    } else {
                        lecturesByUser.put(session, lectures.get(mObject.lectureId))
                        unassignedUsers.remove(session)
                    }
                }
            }

        } else {
            lecturesByUser.get(session).onMessage(msg, session)
        }
    }

    @Override
    void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // TODO Print error
    }

    @Override
    void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        lecturesByUser.remove(session)
        unassignedUsers.remove(session)
    }

    @Override
    boolean supportsPartialMessages() {
        return false
    }

    static boolean addLecture(LectureHandler lecture, int id) {
        if(lectures.containsKey(id)) {
            return false
        }
        lectures.put(id, lecture)
        lecture.setId(id)
        return true
    }

    static void closeLecture(int id) {
        LectureHandler lecture = lectures.get(id)
        closeLecture(lecture)
    }

    static void closeLecture(LectureHandler lecture) {
        lectures.remove(lecture.getId())
        for(WebSocketSession user : lecture.getAllUsers()) {
            lecturesByUser.remove(user)
        }
        lecture.close()
    }

    static boolean isAlive(int id) {
        return lectures.containsKey(id)
    }

}
