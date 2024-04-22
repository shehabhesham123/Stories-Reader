package com.example.readerapp.core.stream

import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException


class FileStream {

    companion object {
        // singleton
        private val stream = FileStream()
        fun instance(): FileStream {
            return stream
        }
    }

    fun createFolder(folderName: String): File? {
        if (isExternalStorageWritable()) {
            // to get the document path (/storage/emulated/0/Documents)
            val documentsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val dir = File(documentsDir, folderName)
            return if (dir.exists() || dir.mkdirs()) {
                dir
            } else null
        }
        return null
    }

    fun createFile(fileName: String, folder: File?, fileContent: String, append: Boolean): File? {
        return if (folder != null && folder.isDirectory) {
            val file = File(folder, fileName)
            try {
                FileOutputStream(file, append).use { fileOutputStream ->
                    fileOutputStream.write(fileContent.toByteArray())
                }
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null // Return null if file creation failed
            }
        } else null
    }

    fun readFileContent(file: File): String {
        val stringBuilder = StringBuilder()
        BufferedReader(FileReader(file)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        }
        return stringBuilder.toString()
    }

    fun getAllFiles(folder: File): List<File> {
        return folder.listFiles()?.toList()!!
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}