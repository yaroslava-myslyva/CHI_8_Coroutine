package com.example.chi_8_coroutine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ListFragment() : Fragment(), CoroutineScope {

    //3) Сделать 2 варианта!!! Используя SupervisorJob и в обычной джобе,
    // запустить корутину через async, сделать паузу корутине на 1000 мс,
    // выбросить эксепшн и обработать его (вывести на экран ошибку загрузки)

    private lateinit var binding: FragmentListBinding
    private var list = mutableListOf<Animal>()
    private val adapter = AnimalAdapter()
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch(coroutineContext) {
            firstRequest()
            setupRecyclerview()

            secondRequest()
            adapter.updateList(list)
        }

    }

    // MainScope()

    private suspend fun firstRequest() {
        Log.e("ttt", "firstRequest start")

        withContext(Dispatchers.IO) {
            val retrofitList = retrofitRequest()
            list.addAll(retrofitList)
        }
        Log.e("ttt", "firstRequest end")
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun secondRequest() = coroutineScope {
        Log.e("ttt", "secondRequest start")
        val firstJob: Job = launch(newFixedThreadPoolContext(3, "pool")) {
            repeat(3) {
                try {
                    delay(500L)
                    val retrofitList = retrofitRequest()
                    list.addAll(retrofitList)
                    Log.e("ttt", "secondRequest end of try")

                } catch (error: CancellationException) {
                    Log.e("ttt", "secondRequest CancellationException")

                } finally {

                }
            }
        }
        delay(2500L)
        firstJob.cancelAndJoin()
        Log.e("ttt", "secondRequest end")

    }

    private fun retrofitRequest(): MutableList<Animal> {
        Log.e("ttt", "retrofitRequest start")

        return try {
            val service = Common.retrofitService
            val retrofitList: MutableList<Animal> =
                service.getResponseItem().execute().body() as MutableList<Animal>
            Log.e("ttt", "retrofitRequest end")

            retrofitList

        } catch (err: Error) {
            Log.e("ttt", "Request error ${err.localizedMessage}")
            mutableListOf()
        }
    }

    private fun setupRecyclerview() {
        Log.e("ttt", "setupRecyclerview start")

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
        Log.e("ttt", "setupRecyclerview end")

    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}
