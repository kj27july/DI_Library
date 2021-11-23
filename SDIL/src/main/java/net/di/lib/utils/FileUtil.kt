package net.di.lib.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

//reference: https://www.zoftino.com/saving-files-to-internal-storage-&-external-storage-in-android
class FileUtil(private val context: Context) {
    private val TAG = "akshay"

    fun writeFile(fileName: String, content: String, update: Boolean): Boolean {
        Log.d(TAG, "writeFile: $fileName")
        return try {
            val outputStream = if (update) {
                context.openFileOutput(fileName, Context.MODE_APPEND)
            } else {
                context.openFileOutput(fileName, Context.MODE_PRIVATE)
            }
            outputStream.write(content.toByteArray())
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            Log.d(TAG, "writeFile: ", e)
            e.printStackTrace()
            false
        }
    }

    fun readFile(fileName: String): String? {
        Log.d(TAG, "readFile: $fileName")
        return try {
            val fileInputStream = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(fileInputStream))
            val sb = StringBuffer()
            var line: String? = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            sb.toString()
        } catch (e: Exception) {
            Log.d(TAG, "readFile: ", e)
            e.printStackTrace()
            null
        }
    }

    private fun isFilePresent(fileName: String): Boolean {
        Log.d(TAG, "isFilePresent: $fileName")
        return File(getPath(fileName)).exists()
    }

    private fun getPath(fileName: String): String {
        return context.filesDir.absolutePath + File.pathSeparator + fileName
    }
}