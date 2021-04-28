package com.android.scorp

import DataSource
import FetchCompletionHandler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.scorp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var concatPeopleList = arrayListOf<String>()
    private lateinit var fetchCompletionHandler: FetchCompletionHandler
    private lateinit var peopleAdapter: PeopleAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var nextPage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        peopleAdapter = PeopleAdapter()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.peopleRv.layoutManager = layoutManager
        binding.peopleRv.adapter = peopleAdapter

        fetchCompletionHandler = { fetchResponse, fetchError ->
            binding.progressBar.visibility = View.GONE
            if (binding.swipe.isRefreshing)
                binding.swipe.isRefreshing = false


            if (fetchResponse != null) {
                binding.swipe.isEnabled = true
                binding.retryBtn.visibility = View.GONE

                if (!fetchResponse?.people.isNullOrEmpty()) {
                    binding.emptyListTv.visibility = View.GONE
                    binding.peopleRv.visibility = View.VISIBLE
                    fetchResponse?.people?.forEach { person ->
                        val concatName = person.fullName + "(" + person.id + ")"
                        concatPeopleList.add(concatName)
                    }
                    peopleAdapter.addToList(concatPeopleList.toMutableList())
                    concatPeopleList.clear()
                } else if (fetchResponse?.people.isNullOrEmpty() && peopleAdapter.itemCount == 0) {
                    binding.peopleRv.visibility = View.GONE
                    binding.emptyListTv.visibility = View.VISIBLE
                }
                nextPage = fetchResponse?.next
            } else {
                binding.retryBtn.visibility = View.VISIBLE
                binding.peopleRv.visibility = View.GONE
                binding.emptyListTv.visibility = View.GONE
                if (fetchError != null) {
                    binding.swipe.isEnabled = false
                    Toast.makeText(this, fetchError.errorDescription, Toast.LENGTH_LONG).show()
                }
            }
        }
        DataSource().fetch(nextPage, fetchCompletionHandler)
        binding.peopleRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val currentItemsCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (currentItemsCount + lastVisibleItem >= peopleAdapter.itemCount && dy > 0) {
                    if (binding.progressBar.visibility == View.GONE) {
                        binding.progressBar.visibility = View.VISIBLE
                        DataSource().fetch(nextPage, fetchCompletionHandler)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        binding.retryBtn.setOnClickListener {
            binding.swipe.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            binding.retryBtn.visibility = View.GONE
            nextPage = null
            peopleAdapter.clearList()
            concatPeopleList.clear()
            DataSource().fetch(nextPage, fetchCompletionHandler)
        }
        binding.swipe.setOnRefreshListener {
            if (binding.swipe.isRefreshing) {
                concatPeopleList.clear()
                peopleAdapter.clearList()
                DataSource().fetch(nextPage, fetchCompletionHandler)
            }
        }
    }

}