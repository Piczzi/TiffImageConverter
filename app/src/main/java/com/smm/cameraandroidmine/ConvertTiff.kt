package com.smm.cameraandroidmine

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import org.beyka.tiffbitmapfactory.CompressionScheme
import org.beyka.tiffbitmapfactory.TiffConverter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ConvertTiff {

    private lateinit var pathIn: String

    fun saveToInternalStorage(imageName: String, image: Bitmap, context: Context) {

        val cw: ContextWrapper = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/documentsToSend
        val directory: File = cw.getDir("imagenesJPG", Context.MODE_PRIVATE)
        // Create documentToSend
        val myPath: File = File(directory, "$imageName.jpg")

        pathIn = myPath.absolutePath
        val fos: FileOutputStream = FileOutputStream(pathIn) // With "FileOutputStream" we can save the image in the storage
        image.compress(Bitmap.CompressFormat.JPEG, 100, fos) // Aquí se guarda la imagen
        fos.close()

        convertToTiff(context, imageName)

    }

    private fun convertToTiff(context: Context, imageName: String) {

        val pathOut = generatePath(context, imageName)

        val options: TiffConverter.ConverterOptions = TiffConverter.ConverterOptions()
        options.throwExceptions = false //Set to true if you want use java exception mechanism;
        options.availableMemory = (128 * 1024 * 1024).toLong() //Available 128Mb for work;
        options.compressionScheme = CompressionScheme.LZW //compression scheme for tiff
        options.appendTiff = false //If set to true - will be created one more tiff directory, otherwise file will be overwritten
        TiffConverter.convertToTiff(pathIn, pathOut, options) { processedPixels, totalPixels ->
            //Log.d("Progress reporter",
            //    "" + String.format("Processed %d pixels from %d",
            //        processedPixels,
            //        totalPixels))
        }

        getBA64Tiff(pathOut)

    }

    private fun getBA64Tiff(path: String): String {
        val file: File = File(path)
        return try {
            val mByt: ByteArray = file.readBytes()
            val mTifBASE64: String = android.util.Base64.encodeToString(mByt, android.util.Base64.DEFAULT)
            mTifBASE64
        } catch (e: IOException) {
            Log.d("ERROR", "Error al obtener el Base64. Message: ${e.message}")
            "ERROR :("
        }
    }

    private fun generatePath(context: Context, imageName: String): String {

        val cw: ContextWrapper = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = cw.getDir("imagenesTIFF", Context.MODE_PRIVATE)
        // Create imageDir
        val myPath: File = File(directory, "$imageName.tif")

        return myPath.absolutePath
    }

}