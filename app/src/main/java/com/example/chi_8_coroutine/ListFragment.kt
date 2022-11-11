package com.example.chi_8_coroutine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chi_8_coroutine.databinding.FragmentListBinding
import com.example.chi_8_coroutine.network.Common
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.util.concurrent.CancellationException
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class ListFragment() : Fragment() {

    private lateinit var binding: FragmentListBinding
    private var list = mutableListOf<Animal>()
    private val adapter = AnimalAdapter()
    private val TAG = "ttt"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val job = Job()
        val supervisorJob = SupervisorJob()

        MainScope().launch(Dispatchers.Main) {
            firstRequest()
            setupRecyclerview()

            secondRequest()
            adapter.updateList(list)
            loadData(CoroutineScope(Dispatchers.Main + job))
            loadData(CoroutineScope(Dispatchers.Main + supervisorJob))
        }
    }

    private suspend fun firstRequest() {
        withContext(Dispatchers.IO) {
            val retrofitList = retrofitRequest()
            list.addAll(retrofitList)
        }
    }

    private fun retrofitRequest(): MutableList<Animal> {
        return try {
            val service = Common.retrofitService
            val retrofitList: MutableList<Animal> =
                service.getResponseItem().execute().body() as MutableList<Animal>
            retrofitList
        } catch (err: Error) {
            Log.e(TAG, "Request error ${err.localizedMessage}")
            mutableListOf()
        }
    }

    private fun setupRecyclerview() {
        adapter.setItems(list)
        binding.animalsList.adapter = adapter
        binding.animalsList.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager(context).orientation
                )
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun secondRequest() = coroutineScope {
        val firstJob: Job = launch(newFixedThreadPoolContext(3, "pool")) {
            repeat(3) {
                launch {
                    try {
                        delay(500L)
                        val retrofitList = retrofitRequest()
                        list.addAll(retrofitList)
                        Log.d(TAG, "secondRequest ${Thread.currentThread()}")
                    } catch (error: CancellationException) {
                        Log.e(TAG, "secondRequest CancellationException")

                    } finally {

                    }
                }
            }
        }
        delay(2000L)
        firstJob.cancelAndJoin()
    }

    private suspend fun loadData(scope: CoroutineScope) = scope.launch {
        async {
            try {
                delay(1000L)
                throw Exception()
            } catch (error: Exception) {
                Toast.makeText(activity, "Failed download", Toast.LENGTH_SHORT).show()
            }
        }.await()
    }
}
