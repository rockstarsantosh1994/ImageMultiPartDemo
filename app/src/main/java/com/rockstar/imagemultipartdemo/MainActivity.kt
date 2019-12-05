package com.rockstar.imagemultipartdemo

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE: Int=1000
    private val IMAGE_CAPTURE_CODE: Int=1001

    private var ivImg: ImageView?=null
    private var imgUri: Uri?=null
    private var btnCaptureImage:AppCompatButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivImg=findViewById(R.id.iv_img)
        btnCaptureImage=findViewById(R.id.btn_captureimg)

        btnCaptureImage?.setOnClickListener(View.OnClickListener {
            //is system is marshmellow or above we need to give runtime permissions to device
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ==PackageManager.PERMISSION_DENIED){
                    //permission not enabled..
                    val permission= arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission,PERMISSION_CODE)

                }else{
                    //permission already granted
                    openCamera()
                }
            }else{
                //system os < marshmello
                openCamera()
            }
        })
    }

    private fun openCamera(){
        val values=ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera")
        imgUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from permission request popup
        when(requestCode){
            PERMISSION_CODE->{
                if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    openCamera()
                }else{
                    //permission from popup denied
                    Toast.makeText(applicationContext,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Called when image was captured from camera intent
        if(resultCode== Activity.RESULT_OK){
            ivImg?.setImageURI(imgUri)
            Toast.makeText(applicationContext,"Image Uri\n"+imgUri,Toast.LENGTH_LONG).show()
        }
    }
}
