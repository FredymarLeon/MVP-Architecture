package com.fredymarleon.mvparchitecture.presenter

import android.util.Log
import com.fredymarleon.mvparchitecture.common.EventBus
import com.fredymarleon.mvparchitecture.common.SportEvent
import com.fredymarleon.mvparchitecture.mainModule.model.MainRepository
import com.fredymarleon.mvparchitecture.mainModule.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainPresenter(private val view: MainActivity) {
    private val repository = MainRepository()
    private lateinit var viewScope: CoroutineScope

    fun onCreate() {
        viewScope = CoroutineScope(Dispatchers.IO + Job())
        onEvent()
    }

    fun onDestroy() {
        viewScope.cancel()
    }

    suspend fun refresh() {
        view.clearAdapter()
        view.showAdUI(true)
        getEvents()
    }

    suspend fun getEvents() {
        view.showProgress(true)
        repository.getEvents()
    }

    suspend fun registerAd() {
        repository.registerAd()
    }

    suspend fun closedAd() {
        repository.closedAd()
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        view.showProgress(true)
        repository.saveResult(result)
    }

    private fun onEvent() {
        viewScope.launch {
            EventBus.instance().subscribeToEvents<SportEvent> { event ->
                this.launch {
                    when (event) {
                        is SportEvent.ResultSuccess -> {
                            view.add(event)
                            view.showProgress(false)
                        }

                        is SportEvent.ResultError -> {
                            view.showSnackbar("Code: ${event.errorCode}, Message: ${event.errorMessage}")
                            view.showProgress(false)
                        }

                        is SportEvent.AdEvent -> {
                            view.showToast("Ad click. Send data to server...")
                        }

                        is SportEvent.SaveEvent -> {
                            view.showToast("Guardado")
                            view.showProgress(false)
                        }

                        is SportEvent.CloseAdEvent -> {
                            view.showAdUI(false)
                            Log.i("Curso-Arquitectura", "Ad was closed. Send data to server...")
                        }
                    }
                }
            }
        }
    }
}