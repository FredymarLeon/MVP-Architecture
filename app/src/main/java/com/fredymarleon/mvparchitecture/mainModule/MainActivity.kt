package com.fredymarleon.mvparchitecture.mainModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fredymarleon.mvparchitecture.EventBus
import com.fredymarleon.mvparchitecture.ResultAdapter
import com.fredymarleon.mvparchitecture.SportEvent
import com.fredymarleon.mvparchitecture.databinding.ActivityMainBinding
import com.fredymarleon.mvparchitecture.getAdEventsInRealtime
import com.fredymarleon.mvparchitecture.getResultEventsInRealtime
import com.fredymarleon.mvparchitecture.someTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

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
            adapter.clear()
            getEvents()
            binding.btnAd.isVisible = true
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    val events = getAdEventsInRealtime()
                    EventBus.instance().publishEvent(events.first())
                }
            }
            setOnLongClickListener { view ->
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    EventBus.instance().publishEvent(SportEvent.CloseAdEvent)
                    view.isVisible = false
                }
                true
            }
        }
    }

    private fun getEvents() {
        lifecycleScope.launch {
            val events = getResultEventsInRealtime()
            events.forEach { event ->
                delay(someTime().milliseconds)
                EventBus.instance().publishEvent(event)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.srlResults.isRefreshing = true
        getEvents()
    }

    override fun onClick(result: SportEvent.ResultSuccess) {
        binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            // EventBus.instance().publishEvent(SportEvent.SaveEvent)
           // SportService.instance().saveResult(result)
        }
    }
}