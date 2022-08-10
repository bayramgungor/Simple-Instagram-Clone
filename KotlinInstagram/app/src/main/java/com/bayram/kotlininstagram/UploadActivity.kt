package com.bayram.kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bayram.kotlininstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.sql.Timestamp
import java.time.Instant.now
import java.time.Year.now
import java.time.YearMonth.now
import java.util.*

class UploadActivity : AppCompatActivity() {


    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? =null
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage

    private lateinit var binding:ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        registerLauncher()

        auth=Firebase.auth
        firestore=FirebaseFirestore.getInstance()
        storage=FirebaseStorage.getInstance()


    }


    fun upload(view: View){

        val uuid=UUID.randomUUID()
        val imageName="$uuid.jpg"

        val refence=storage.reference
        val imageRefence=refence.child("image").child(imageName)

        if (selectedPicture!=null){
            imageRefence.putFile(selectedPicture!!).addOnSuccessListener{

                val uploadPicturesRefence=storage.reference.child("image").child(imageName)
                uploadPicturesRefence.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    val postMap= hashMapOf<String,Any>()
                    if (auth.currentUser!=null){
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",com.google.firebase.Timestamp.now())

                        firestore.collection("post").add(postMap).addOnSuccessListener {

                            finish()

                        }.addOnFailureListener {
                            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }

                }



            }.addOnFailureListener{

                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }



    }

    fun selectImage(view:View){

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }
        }else{
            val intentToGallert=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallert)
        }

    }

    fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK){
                val intentFromResult=result.data
               if (intentFromResult!=null){
                   selectedPicture=intentFromResult.data
                   selectedPicture?.let {
                       binding.imageView.setImageURI(it)
                   }



            }

            }

        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){

                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                Toast.makeText(this,"permission needed",Toast.LENGTH_LONG).show()
            }

        }


    }
}