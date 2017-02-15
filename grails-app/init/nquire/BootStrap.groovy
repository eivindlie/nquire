package nquire

import nquire.User;
import nquire.Role;
import nquire.UserRole;

class BootStrap {

    def init = { servletContext ->
        Role lecturerRole = new Role(authority: 'ROLE_LECTURER').save(failOnError: true)

        UserRole.withSession {
            it.flush()
            it.clear()
        }
    }
    def destroy = {
    }
}
