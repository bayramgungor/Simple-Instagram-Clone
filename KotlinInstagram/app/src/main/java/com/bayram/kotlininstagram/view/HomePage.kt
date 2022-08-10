package com.bayram.kotlininstagram.view

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayram.kotlininstagram.R
import com.bayram.kotlininstagram.UploadActivity
import com.bayram.kotlininstagram.adaptor.HomePageRecyclerAdaptor
import com.bayram.kotlininstagram.databinding.ActivityHomePageBinding
import com.bayram.kotlininstagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class HomePage : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var binding:ActivityHomePageBinding
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var homePostAdaptor:HomePageRecyclerAdaptor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomePageBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        db= FirebaseFirestore.getInstance()
        auth=Firebase.auth
        postArrayList=ArrayList<Post>()
        getData()

        binding.recylerView.layoutManager=LinearLayoutManager(this)
        homePostAdaptor= HomePageRecyclerAdaptor(postArrayList)
        binding.recylerView.adapter=homePostAdaptor

    }

    private fun getData(){
        db.collection("post").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()

            }else{
                if (value!=null){
                    if (!value.isEmpty){

                        val documents=value.documents
                        postArrayList.clear()

                        for (document in documents){
                            val comment=document.get("comment")as String
                            val userEmail=document.get("userEmail")as String
                            val downloadUrl=document.get("downloadUrl") as String

                            println(comment)
                            val post=Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)


                        }
                        homePostAdaptor.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId== R.id.add_post){
            val intent=Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }else if(item.itemId== R.id.signout){

            auth.signOut() 
            val intent=Intent(this@HomePage, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        return super.onOptionsItemSelected(item)
    }
}