package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.session.SessionManager

class SessionRepository(
    private val sessionManager: SessionManager
) {

    fun obtenerUsuarioLogueado(): Int? {
        return sessionManager.getUserId()
    }

    fun cerrarSesion() {
        sessionManager.clearSession()
    }
}