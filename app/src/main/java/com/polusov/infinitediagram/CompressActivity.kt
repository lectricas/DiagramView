package com.polusov.infinitediagram

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_compress.compressFile
import kotlinx.android.synthetic.main.activity_compress.deleteFile
import kotlinx.android.synthetic.main.activity_compress.photoFromCamera
import kotlinx.android.synthetic.main.activity_compress.showFile
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class CompressActivity : PmSupportActivity<CompressPm>() {

    companion object {
        const val PHOTO = 3
        private val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".module.fileprovider"
        private val FILE_PATH = "/Pictures/DocScanner/"
    }

    private lateinit var rxPermission: RxPermissions

    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compress)
        rxPermission = RxPermissions(this)

        photoFromCamera.setOnClickListener {
            rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    startActivityForResult(takePicture(), PHOTO)
                }
        }

        showFile.setOnClickListener {
            openFile(File(filePath))
        }

        deleteFile.setOnClickListener {
            deleteFile(File(filePath))
        }

        compressFile.setOnClickListener {
            compressFile(File(filePath))
        }
    }

    override fun onBindPresentationModel(pm: CompressPm) {}

    override fun providePresentationModel(): CompressPm {
        return CompressPm()
    }

    private fun takePicture(): Intent {
        val photoURI: Uri = FileProvider.getUriForFile(
            this@CompressActivity,
            FILE_PROVIDER_AUTHORITY,
            createImageFile("jpg")
        )
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    }

    private fun createImageFile(extension: String): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(Environment.getExternalStorageDirectory(), FILE_PATH)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "$timeStamp.$extension").apply {
            filePath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            showFile.visible = filePath.isNotBlank()
            deleteFile.visible = filePath.isNotBlank()
            compressFile.visible = filePath.isNotBlank()
            Toast.makeText(this, "File is saved: $filePath", Toast.LENGTH_LONG).show()
        }
    }

    private fun openFile(file: File) {
        Timber.d("absolutePath = ${file.absolutePath}")
        val fileUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file)
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (file.absolutePath.contains(".jpg") || file.absolutePath.contains(".jpeg")) {
                intent.setDataAndType(fileUri, "image/jpeg")
            }

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFile(file: File) {
        file.delete()
        filePath = ""
        showFile.visible = filePath.isNotBlank()
        deleteFile.visible = filePath.isNotBlank()
        compressFile.visible = filePath.isNotBlank()
    }

    private fun compressFile(file: File) {
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 2
        val bitmap = Convert.toGrayscale(BitmapFactory.decodeFile(file.absolutePath, bitmapOptions))

        val newFile = createImageFile("jpg")
        val os = FileOutputStream(newFile.absolutePath)
        bitmap.compress(JPEG, 50, os)

        val oldExif = ExifInterface(file.absolutePath)
        val exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        if (exifOrientation != null) {
            val newExif = ExifInterface(newFile.absolutePath)
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
            newExif.saveAttributes()
        }
        filePath = newFile.absolutePath
        file.delete()
    }
}