package com.example.myweatherapp.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.myweatherapp.data.remote.Daily
import com.example.myweatherapp.databinding.CardDailyBinding
import com.example.myweatherapp.utils.getFormattedDate
import com.example.myweatherapp.utils.getImageFromUrl

class HomeAdapter():ListAdapter<Daily,HomeAdapter.HomeViewHolder>(ComparatorDiffUtil()) {



    private var scaleType:String = "C"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.HomeViewHolder {

        val binding = CardDailyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val daily = getItem(position)
        daily?.let { holder.bind(it) }
    }

    inner class HomeViewHolder(private val binding: CardDailyBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(it: Daily) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.date.text = ""+getFormattedDate(it.dt)
            }

            binding.img.setImageResource(getImageFromUrl(it.weather[0].icon))
           // binding.minMax.text = ""+it.temp.min.toInt() +" / "+it.temp.max.toInt()+" °C"
            binding.minMax.text = ""+it.temp.min.toInt() +" / "+it.temp.max.toInt()+" °"+scaleType
        }


    }
    fun changeStatus(_scaleType:String){
       scaleType=_scaleType
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }
    }


}