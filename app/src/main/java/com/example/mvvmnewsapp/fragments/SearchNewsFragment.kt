package com.example.mvvmnewsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmnewsapp.MainActivity
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.adapter.NewsAdapter
import com.example.mvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAy
import com.example.mvvmnewsapp.util.NewsViewModel
import com.example.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

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
            findNavController().navigate(R.id.action_searchNewsFragment2_to_articleFragment,bundle)
        }

        var job:Job?=null
        etSearch.addTextChangedListener {
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAy)
                it?.let {
                    if (it.toString().isNotEmpty()){
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {response ->

            when(response){
                is Resource.Success ->{
                    hideProgressBar()

                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        Log.d("SearchNewsFragment","Response=${response}")

                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let {
                        Log.e("SearchNewsFragment","An error occured=${it}")
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
        rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}