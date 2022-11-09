package com.example.chi_8_coroutine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chi_8_coroutine.databinding.FragmentListBinding
import com.example.chi_8_coroutine.network.Common
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import kotlin.concurrent.thread

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private var list = mutableListOf<Animal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread(start = true) {
            val okHttpList = okHttpRequest()
            val retrofitList = retrofitRequest()

            list.addAll(okHttpList)
            list.addAll(retrofitList)
            Log.d("ttt", "list size = ${list.size}")
            activity?.runOnUiThread{
                setupRecyclerview()
            }
        }
    }

    private fun okHttpRequest(): MutableList<Animal> {
        return try {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url(URL)
                .build()
            val response: Response = client.newCall(request).execute()
            val builder = GsonBuilder()
            val gson = builder.create()
            val animalListType: Type = object : TypeToken<ArrayList<Animal>?>() {}.type

            val okHttpList: MutableList<Animal> =
                gson.fromJson(response.body?.string().toString(), animalListType)
            okHttpList
        } catch (err: Error) {
            Log.e("ttt", "Request error ${err.localizedMessage}")
            mutableListOf()
        }
    }

    private fun retrofitRequest(): MutableList<Animal> {
        return try {
            val service = Common.retrofitService
            val retrofitList: MutableList<Animal> =
                service.getResponseItem().execute().body() as MutableList<Animal>
            retrofitList
        } catch (err: Error) {
            Log.e("ttt", "Request error ${err.localizedMessage}")
            mutableListOf()
        }
    }

    private fun setupRecyclerview() {
        val adapter = AnimalAdapter()
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

    companion object {
        private const val URL = "https://zoo-animal-api.herokuapp.com/animals/rand/10/"
    }

}
