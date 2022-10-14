package com.example.myweatherapp.ui.bookmarked

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.data.models.FavCity
import com.example.myweatherapp.databinding.CardFavCityBinding




class BookmarkAdapter(private val onClickListener: OnClickListener):ListAdapter<FavCity,BookmarkAdapter.BookmarkViewHolder>(ComparatorDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkAdapter.BookmarkViewHolder {

        val binding = CardFavCityBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return BookmarkViewHolder(binding)
    }
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val fav = getItem(position)
        /*fav?.let { holder.bind(it) }*/
        holder.bind(fav)

    }

    inner class BookmarkViewHolder(private val binding: CardFavCityBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favCity: FavCity) {
            binding.cfvCityname.text = favCity.title
            binding.root.setOnClickListener{
                onClickListener.onClick(favCity)
            }
        }



    }



    class ComparatorDiffUtil : DiffUtil.ItemCallback<FavCity>() {
        override fun areItemsTheSame(oldItem: FavCity, newItem: FavCity): Boolean {
            return oldItem.title == newItem.title
        }
        override fun areContentsTheSame(oldItem: FavCity, newItem: FavCity): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (favCity: FavCity) -> Unit) {
        fun onClick(favCity: FavCity) = clickListener(favCity)
    }



}