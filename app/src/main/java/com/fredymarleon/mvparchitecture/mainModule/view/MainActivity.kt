package com.fredymarleon.mvparchitecture.mainModule.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fredymarleon.mvparchitecture.SportEvent
import com.fredymarleon.mvparchitecture.databinding.ActivityMainBinding
import com.fredymarleon.mvparchitecture.presenter.MainPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = MainPresenter(this)
        presenter.onCreate()

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)

    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            //adapter.clear()
            //getEvents()
            //binding.btnAd.isVisible = true
            lifecycleScope.launch {
                presenter.refresh()
            }

        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    //binding.srlResults.isRefreshing = true
//                    val events = getAdEventsInRealtime()
//                    EventBus.instance().publishEvent(events.first())
                    presenter.registerAd()
                }
            }
            setOnLongClickListener { view ->
                lifecycleScope.launch {
                    // binding.srlResults.isRefreshing = true
//                    EventBus.instance().publishEvent(SportEvent.CloseAdEvent)
//                    view.isVisible = false
                    presenter.closedAd()
                }
                true
            }
        }
    }

//    private fun getEvents() {
//        lifecycleScope.launch {
////            val events = getResultEventsInRealtime()
////            events.forEach { event ->
////                delay(someTime().milliseconds)
////                EventBus.instance().publishEvent(event)
////            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        // binding.srlResults.isRefreshing = true
        //getEvents()
        lifecycleScope.launch { presenter.getEvents() }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onClick(result: SportEvent.ResultSuccess) {
        //binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            // EventBus.instance().publishEvent(SportEvent.SaveEvent)
            // SportService.instance().saveResult(result)
            presenter.saveResult(result)
        }
    }

    /*
    * ViewLayer
    * */

    fun add(event: SportEvent.ResultSuccess) {
        adapter.add(event)
    }

    fun clearAdapter() {
        adapter.clear()
    }

    fun showAdUI(isVisible: Boolean) {
        binding.btnAd.isVisible = isVisible
    }

    fun showProgress(isVisible: Boolean) {
        binding.srlResults.isRefreshing = isVisible
    }

    fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
    }

    fun showSnackbar(msj: String) {
        Snackbar.make(binding.root, msj, Snackbar.LENGTH_LONG).show()
    }
}