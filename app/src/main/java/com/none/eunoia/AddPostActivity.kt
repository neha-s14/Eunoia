package com.none.eunoia

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.util.*

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")
        save_new_post_btn.setOnClickListener {
            uploadImage()
        }
        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
when{
    imageUri==null->
    {
        Toast.makeText(this,"Select an image",Toast.LENGTH_SHORT).show()
    }
    description_post.text.toString()==""->
    {
        Toast.makeText(this,"You have not added description",Toast.LENGTH_SHORT).show()
    }
    else->{
        val progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Uploading Post")
        progressDialog.setMessage("Please Wait!")
        progressDialog.show()

        val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString()!!+".jpg")
        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            {
                task.exception?.let { it ->
                    throw  it
                    progressDialog.dismiss()
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener(OnCompleteListener { task->
            if(task.isSuccessful)
            {
                val downloadUrl=task.result
                myUrl=downloadUrl.toString()
                val ref= FirebaseDatabase.getInstance().reference.child("Posts")
                val postId=ref.push().key
                val postMap = HashMap<String, Any>()
                postMap.put("postid", postId!!)
                postMap.put("description", description_post.text.toString())
                postMap.put("publisher", FirebaseAuth.getInstance().currentUser!!.uid)
                postMap.put("postimage", myUrl)

                ref.child(postId).updateChildren(postMap)


                Toast.makeText(this,"Post has been uploaded successfully",Toast.LENGTH_SHORT).show()
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                progressDialog.dismiss()
            }
            else
            {
                progressDialog.dismiss()
            }

        })
    }
}
    }
}