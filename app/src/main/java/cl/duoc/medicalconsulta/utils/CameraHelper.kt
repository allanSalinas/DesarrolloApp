package cl.duoc.medicalconsulta.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class para gestionar el acceso a la cámara y galería
 * Proporciona métodos para tomar fotos, seleccionar imágenes y convertirlas a Base64
 */
class CameraHelper(private val context: Context) {

    companion object {
        private const val TAG = "CameraHelper"

        // Request codes para los permisos
        const val REQUEST_CAMERA_PERMISSION = 100
        const val REQUEST_STORAGE_PERMISSION = 101
        const val REQUEST_IMAGE_CAPTURE = 200
        const val REQUEST_IMAGE_PICK = 201

        // Directorio para imágenes temporales
        private const val TEMP_IMAGE_DIRECTORY = "MedicalConsulta"

        // Calidad de compresión para JPEG
        private const val JPEG_QUALITY = 85

        // Tamaño máximo para las imágenes (ancho/alto)
        private const val MAX_IMAGE_DIMENSION = 1920
    }

    private var currentPhotoPath: String? = null
    private var currentPhotoUri: Uri? = null

    /**
     * Verifica si la aplicación tiene permisos de cámara
     *
     * @return true si tiene permisos, false en caso contrario
     */
    fun hasCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Verifica si la aplicación tiene permisos de almacenamiento
     *
     * @return true si tiene permisos, false en caso contrario
     */
    fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ no requiere READ_EXTERNAL_STORAGE para MediaStore
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12
            true // Scoped storage no requiere permisos para leer propias imágenes
        } else {
            // Android 9 y anteriores
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Solicita permisos de cámara
     *
     * @param activity Activity desde donde se solicita el permiso
     */
    fun requestCameraPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    /**
     * Solicita permisos de almacenamiento
     *
     * @param activity Activity desde donde se solicita el permiso
     */
    fun requestStoragePermission(activity: Activity) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // En Android 10+ no se necesita permiso explícito para leer imágenes propias
            return
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            activity,
            permissions,
            REQUEST_STORAGE_PERMISSION
        )
    }

    /**
     * Crea un Intent para tomar una foto con la cámara
     *
     * @return Intent para lanzar la cámara, o null si hay un error
     */
    fun createTakePictureIntent(): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            // Crear archivo temporal para la foto
            val photoFile = createImageFile()
            photoFile?.let {
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    it
                )
                currentPhotoUri = photoUri
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                return takePictureIntent
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error creating image file", e)
            e.printStackTrace()
        }

        return null
    }

    /**
     * Crea un Intent para seleccionar una imagen de la galería
     *
     * @return Intent para lanzar el selector de imágenes
     */
    fun createPickImageIntent(): Intent {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        return Intent.createChooser(pickImageIntent, "Seleccionar imagen")
    }

    /**
     * Crea un URI para una nueva imagen de cámara
     *
     * @return URI para la nueva imagen, o null si hay un error
     */
    fun createImageUri(): Uri? {
        return try {
            val photoFile = createImageFile()
            photoFile?.let {
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    it
                )
                currentPhotoUri = photoUri
                photoUri
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error creating image URI", e)
            e.printStackTrace()
            null
        }
    }

    /**
     * Crea un archivo temporal para guardar la imagen
     *
     * @return File temporal creado
     */
    private fun createImageFile(): File? {
        return try {
            // Crear nombre de archivo único con timestamp
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "MEDICAL_${timeStamp}_"

            // Directorio de almacenamiento
            val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ usar directorio específico de la app
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TEMP_IMAGE_DIRECTORY)
            } else {
                // Android 9 y anteriores
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMP_IMAGE_DIRECTORY)
            }

            // Crear directorio si no existe
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            // Crear archivo temporal
            File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            ).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error creating image file", e)
            null
        }
    }

    /**
     * Obtiene el URI de la foto tomada
     *
     * @return URI de la foto, o null si no hay foto
     */
    fun getCurrentPhotoUri(): Uri? = currentPhotoUri

    /**
     * Obtiene la ruta del archivo de la foto tomada
     *
     * @return Ruta del archivo, o null si no hay foto
     */
    fun getCurrentPhotoPath(): String? = currentPhotoPath

    /**
     * Convierte una imagen a Base64
     *
     * @param uri URI de la imagen
     * @param maxDimension Dimensión máxima (ancho/alto) para redimensionar
     * @return String en formato Base64, o null si hay error
     */
    fun convertImageToBase64(uri: Uri, maxDimension: Int = MAX_IMAGE_DIMENSION): String? {
        try {
            val bitmap = loadAndResizeBitmap(uri, maxDimension) ?: return null

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Reciclar bitmap para liberar memoria
            bitmap.recycle()

            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting image to Base64", e)
            e.printStackTrace()
            return null
        }
    }

    /**
     * Carga y redimensiona un bitmap desde un URI
     *
     * @param uri URI de la imagen
     * @param maxDimension Dimensión máxima permitida
     * @return Bitmap redimensionado y orientado correctamente
     */
    private fun loadAndResizeBitmap(uri: Uri, maxDimension: Int): Bitmap? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // Primero, obtener las dimensiones de la imagen sin cargarla en memoria
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calcular el factor de escala
            var scaleFactor = 1
            val width = options.outWidth
            val height = options.outHeight

            if (width > maxDimension || height > maxDimension) {
                scaleFactor = Math.max(
                    width / maxDimension,
                    height / maxDimension
                )
            }

            // Cargar la imagen con el factor de escala
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
            }

            val scaledInputStream = context.contentResolver.openInputStream(uri) ?: return null
            var bitmap = BitmapFactory.decodeStream(scaledInputStream, null, scaledOptions)
            scaledInputStream.close()

            // Corregir la orientación de la imagen si es necesario
            bitmap = bitmap?.let { correctImageOrientation(it, uri) }

            return bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error loading and resizing bitmap", e)
            e.printStackTrace()
            return null
        }
    }

    /**
     * Corrige la orientación de la imagen basándose en los datos EXIF
     *
     * @param bitmap Bitmap original
     * @param uri URI de la imagen
     * @return Bitmap con la orientación corregida
     */
    private fun correctImageOrientation(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

            // Reciclar el bitmap original si es diferente del rotado
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }

            return rotatedBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error correcting image orientation", e)
            return bitmap
        }
    }

    /**
     * Guarda una imagen temporalmente en el almacenamiento
     *
     * @param bitmap Bitmap a guardar
     * @return URI de la imagen guardada, o null si hay error
     */
    fun saveImageTemporarily(bitmap: Bitmap): Uri? {
        try {
            val file = createImageFile() ?: return null

            context.contentResolver.openOutputStream(Uri.fromFile(file))?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
            }

            return Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image temporarily", e)
            e.printStackTrace()
            return null
        }
    }

    /**
     * Elimina el archivo temporal de la foto actual
     */
    fun deleteTempImage() {
        currentPhotoPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    Log.d(TAG, "Temporary image deleted: $path")
                } else {
                    Log.d(TAG, "Temporary image file does not exist: $path")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting temporary image", e)
            }
        }
        currentPhotoPath = null
        currentPhotoUri = null
    }

    /**
     * Limpia todas las imágenes temporales del directorio
     */
    fun cleanupTempImages() {
        try {
            val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TEMP_IMAGE_DIRECTORY)
            } else {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMP_IMAGE_DIRECTORY)
            }

            if (storageDir.exists() && storageDir.isDirectory) {
                storageDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.startsWith("MEDICAL_")) {
                        file.delete()
                        Log.d(TAG, "Cleaned up temp file: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up temporary images", e)
        }
    }

    /**
     * Obtiene el tamaño del archivo de imagen en KB
     *
     * @param uri URI de la imagen
     * @return Tamaño en KB, o -1 si hay error
     */
    fun getImageSizeInKB(uri: Uri): Long {
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                val fileSize = descriptor.statSize
                return fileSize / 1024
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting image size", e)
        }
        return -1
    }
}
