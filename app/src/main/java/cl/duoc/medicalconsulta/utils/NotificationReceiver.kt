package cl.duoc.medicalconsulta.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BroadcastReceiver que recibe las alarmas programadas para recordatorios de citas
 * Cuando se dispara la alarma, muestra la notificación de recordatorio usando NotificationHelper
 */
class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    /**
     * Se ejecuta cuando se recibe el broadcast de la alarma programada
     *
     * @param context Contexto de la aplicación
     * @param intent Intent con los datos de la cita
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e(TAG, "Context or Intent is null")
            return
        }

        try {
            // Extraer los datos de la cita del Intent
            val appointmentId = intent.getIntExtra(NotificationHelper.EXTRA_APPOINTMENT_ID, -1)
            val doctorName = intent.getStringExtra(NotificationHelper.EXTRA_DOCTOR_NAME) ?: "el médico"
            val specialty = intent.getStringExtra(NotificationHelper.EXTRA_SPECIALTY) ?: "Consulta médica"
            val date = intent.getStringExtra(NotificationHelper.EXTRA_DATE) ?: ""
            val time = intent.getStringExtra(NotificationHelper.EXTRA_TIME) ?: ""

            Log.d(TAG, "Received alarm for appointment ID: $appointmentId")
            Log.d(TAG, "Doctor: $doctorName, Specialty: $specialty, Date: $date, Time: $time")

            // Validar que se hayan recibido los datos necesarios
            if (appointmentId == -1) {
                Log.e(TAG, "Invalid appointment ID")
                return
            }

            // Crear instancia de NotificationHelper y mostrar la notificación de recordatorio
            val notificationHelper = NotificationHelper(context)

            // Verificar permisos antes de mostrar la notificación
            if (notificationHelper.hasNotificationPermission() &&
                notificationHelper.areNotificationsEnabled()) {

                notificationHelper.showAppointmentReminderNotification(
                    appointmentId = appointmentId,
                    doctorName = doctorName,
                    specialty = specialty,
                    time = time
                )

                Log.d(TAG, "Reminder notification shown successfully for appointment $appointmentId")
            } else {
                Log.w(TAG, "Notification permission not granted or notifications disabled")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error showing reminder notification", e)
            e.printStackTrace()
        }
    }
}
