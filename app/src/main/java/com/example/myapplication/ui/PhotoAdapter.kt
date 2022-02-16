package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Photo
import com.example.myapplication.util.load

class PhotoAdapter(
    private val clickListener: (item: Photo, sharedImageView: ImageView, transitionName: String) -> Unit,
    diffCallback: DiffUtil.ItemCallback<Photo>
) : PagingDataAdapter<Photo, PhotoAdapter.ItemViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Photo?,
            clickListener: (item: Photo, sharedImageView: ImageView, transitionName: String) -> Unit
        ) = with(itemView) {
            item?.let {
                val imageView = itemView.findViewById<ImageView>(R.id.iv_photo)
                val textView = itemView.findViewById<TextView>(R.id.tv_title)
                textView.text = it.title
                imageView.load(it.server, it.id, it.secret)
                imageView.transitionName = it.id
                itemView.setOnClickListener { _ ->
                    clickListener(it, imageView, it.id)
                }
            }
        }
    }
}

object PhotoComparator : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}