package com.bayram.kotlininstagram.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayram.kotlininstagram.databinding.RecyclerRowBinding
import com.bayram.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class HomePageRecyclerAdaptor(val posList:ArrayList<Post>):RecyclerView.Adapter<HomePageRecyclerAdaptor.PostHolder> (){
    class PostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text=posList.get(position).email
        holder.binding.recyclerCommentText.text=posList.get(position).comment
        Picasso.get().load(posList.get(position).downloadUrl).into(holder.binding.recyclerImageView)

    }

    override fun getItemCount(): Int {
        return posList.size
    }
}