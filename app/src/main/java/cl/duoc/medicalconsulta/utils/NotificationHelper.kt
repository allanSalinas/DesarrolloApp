package cl.duoc.medicalconsulta.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.duoc.medicalconsulta.R
import java.util.Calendar

/**
 * Helper class para gestionar las notificaciones de la aplicación Medical Consulta
 * Gestiona la creación de canales, envío de notificaciones y programación de recordatorios
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "medical_consulta_channel"
        const val CHANNEL_NAME = "Notificaciones de Citas"
        const val CHANNEL_DESCRIPTION = "Notificaciones para recordatorios de citas médicas"

        const val NOTIFICATION_ID_BASE = 1000
        const val EXTRA_APPOINTMENT_ID = "appointment_id"
        const val EXTRA_DOCTOR_NAME = "doctor_name"
        const val EXTRA_SPECIALTY = "specialty"
        const val EXTRA_DATE = "date"
        const val EXTRA_TIME = "time"

        // Tiempo en milisegundos para el recordatorio (1 hora antes)
        const val REMINDER_TIME_BEFORE = 60 * 60 * 1000L // 1 hora en milisegundos
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificaciones para Android O y superior
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setShowBadge(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra una notificación cuando se agenda una cita
     *
     * @param appointmentId ID único de la cita
     * @param doctorName Nombre del doctor
     * @param specialty Especialidad médica
     * @param date Fecha de la cita
     * @param time Hora de la cita
     */
    fun showAppointmentScheduledNotification(
        appointmentId: Int,
        doctorName: String,
        specialty: String,
        date: String,
        time: String
    ) {
        if (!hasNotificationPermission()) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Cita Agendada Exitosamente")
            .setContentText("Cita con $doctorName ($specialty)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Su cita con el Dr. $doctorName ha sido agendada.\n" +
                            "Especialidad: $specialty\n" +
                            "Fecha: $date\n" +
                            "Hora: $time\n\n" +
                            "Recibirá un recordatorio 1 hora antes de su cita.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + appointmentId, notification)
    }

    /**
     * Muestra una notificación de recordatorio de cita
     *
     * @param appointmentId ID único de la cita
     * @param doctorName Nombre del doctor
     * @param specialty Especialidad médica
     * @param time Hora de la cita
     */
    fun showAppointmentReminderNotification(
        appointmentId: Int,
        doctorName: String,
        specialty: String,
        time: String
    ) {
        if (!hasNotificationPermission()) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Recordatorio de Cita Médica")
            .setContentText("Su cita es en 1 hora")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Recordatorio: Su cita con el Dr. $doctorName está programada para las $time.\n\n" +
                            "Especialidad: $specialty\n" +
                            "Hora: $time\n\n" +
                            "Por favor, llegue 10 minutos antes de su cita.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + appointmentId + 5000, notification)
    }

    /**
     * Programa un recordatorio para 1 hora antes de la cita
     *
     * @param appointmentId ID único de la cita
     * @param doctorName Nombre del doctor
     * @param specialty Especialidad médica
     * @param date Fecha de la cita
     * @param time Hora de la cita (formato HH:mm)
     * @return true si el recordatorio fue programado exitosamente
     */
    fun scheduleAppointmentReminder(
        appointmentId: Int,
        doctorName: String,
        specialty: String,
        date: String,
        time: String
    ): Boolean {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Crear el intent para el broadcast receiver
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                putExtra(EXTRA_DOCTOR_NAME, doctorName)
                putExtra(EXTRA_SPECIALTY, specialty)
                putExtra(EXTRA_DATE, date)
                putExtra(EXTRA_TIME, time)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appointmentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Calcular el tiempo para la alarma (1 hora antes de la cita)
            val appointmentTimeMillis = parseAppointmentDateTime(date, time)
            val reminderTimeMillis = appointmentTimeMillis - REMINDER_TIME_BEFORE

            // Verificar que el recordatorio sea en el futuro
            if (reminderTimeMillis > System.currentTimeMillis()) {
                // Programar la alarma
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                }
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Cancela el recordatorio programado de una cita
     *
     * @param appointmentId ID único de la cita
     */
    fun cancelAppointmentReminder(appointmentId: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appointmentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            // También cancelar la notificación si existe
            notificationManager.cancel(NOTIFICATION_ID_BASE + appointmentId + 5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Verifica si la aplicación tiene permisos para mostrar notificaciones
     *
     * @return true si tiene permisos, false en caso contrario
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // En versiones anteriores a Android 13, los permisos se otorgan automáticamente
            true
        }
    }

    /**
     * Verifica si las notificaciones están habilitadas para la aplicación
     *
     * @return true si están habilitadas, false en caso contrario
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Convierte la fecha y hora de la cita a milisegundos
     *
     * @param date Fecha en formato dd/MM/yyyy o yyyy-MM-dd
     * @param time Hora en formato HH:mm
     * @return Tiempo en milisegundos
     */
    private fun parseAppointmentDateTime(date: String, time: String): Long {
        val calendar = Calendar.getInstance()

        try {
            // Parsear fecha (asumiendo formato dd/MM/yyyy o yyyy-MM-dd)
            val dateParts = if (date.contains("/")) {
                date.split("/")
            } else {
                date.split("-")
            }

            val day: Int
            val month: Int
            val year: Int

            if (dateParts[0].length == 4) {
                // Formato yyyy-MM-dd
                year = dateParts[0].toInt()
                month = dateParts[1].toInt() - 1 // Calendar usa 0-11 para meses
                day = dateParts[2].toInt()
            } else {
                // Formato dd/MM/yyyy
                day = dateParts[0].toInt()
                month = dateParts[1].toInt() - 1
                year = dateParts[2].toInt()
            }

            // Parsear hora (formato HH:mm)
            val timeParts = time.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            calendar.set(year, month, day, hour, minute, 0)
            calendar.set(Calendar.MILLISECOND, 0)

        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de error, devolver tiempo actual + 2 horas
            calendar.timeInMillis = System.currentTimeMillis() + (2 * 60 * 60 * 1000)
        }

        return calendar.timeInMillis
    }

    /**
     * Cancela todas las notificaciones activas de la aplicación
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
