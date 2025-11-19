package cl.duoc.app.model.data.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}