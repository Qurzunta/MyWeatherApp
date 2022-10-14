package com.example.myweatherapp.ui.bookmarked


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.myweatherapp.WeatherAppDatabase
import com.example.myweatherapp.dao.FavCitiesDao
import com.example.myweatherapp.data.models.FavCity
import com.example.myweatherapp.databinding.FragmentBookmarkedBinding
import kotlinx.coroutines.launch


class bookmarkedFragment : Fragment() {

    private var _binding: FragmentBookmarkedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookmarkAdapter
    private lateinit var dao: FavCitiesDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarkedBinding.inflate(inflater, container, false)

        // Initialize the DAO..
        dao = WeatherAppDatabase.getDatabase(requireContext()).favCitiesDao()


        binding.fbBack.setOnClickListener{
            findNavController().navigateUp()
        }

        adapter = BookmarkAdapter(BookmarkAdapter.OnClickListener{

            val bundle = Bundle()
            bundle.putString("city", ""+it.title)
            bundle.putString("lat", ""+it.lat)
            bundle.putString("lon", ""+it.lon)
            findNavController().navigate(com.example.myweatherapp.R.id.action_bookmarkedFragment_to_homeFragment, bundle)

            //Toast.makeText(activity,"Clicked : "+it.title,Toast.LENGTH_SHORT).show()
            //findNavController().navigate(R.id.action_bookmarkedFragment_to_homeFragment)
        })
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fbRecycler.layoutManager = LinearLayoutManager(activity)
        binding.fbRecycler.adapter = adapter

        lifecycleScope.launch{

            var lst: List<FavCity> = dao.getFavCities()
            adapter.submitList(lst)

        }
    }

}