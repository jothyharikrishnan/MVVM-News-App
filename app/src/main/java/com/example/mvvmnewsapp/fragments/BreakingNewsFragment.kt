package com.example.mvvmnewsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmnewsapp.MainActivity
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.adapter.NewsAdapter
import com.example.mvvmnewsapp.util.NewsViewModel
import com.example.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=(activity as MainActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {response ->

            when(response){
                is Resource.Success ->{
                    hideProgressBar()

                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                     //   Log.d("BreakingNews","Response=${response}")

                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let {
                        Log.e("SearchNewsFragment","An error occured=$it")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()

                }
            }


        })

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility=View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility=View.VISIBLE
    }

    private fun setUpRecyclerView(){
        newsAdapter= NewsAdapter()
        rvBreakingNews.apply {
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
        }
    }
}