package com.oktydeniz.k_foursquare.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.oktydeniz.k_foursquare.R
import com.oktydeniz.k_foursquare.databinding.ActivityStartingBinding
import com.parse.ParseObject
import com.parse.ParseQuery

class StartingActivity : AppCompatActivity() {

    private val TAG = "StartingActivity"
    var nameArray = ArrayList<String>()
    private lateinit var binding: ActivityStartingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        binding.showMapLists.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(applicationContext, DetailActivity::class.java)
            intent.putExtra("name", nameArray[position])
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addPlace -> {
                startActivity(Intent(this, AddNewPlace::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData() {
        val arrayAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, nameArray)
        binding.showMapLists.adapter = arrayAdapter

        val query = ParseQuery.getQuery<ParseObject>("Locations")
        query.findInBackground { objects, e ->
            if (e != null) {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (objects.size > 0) {
                    nameArray.clear()
                    objects.forEach {
                        val name = it.get("name") as String
                        Log.i(TAG, "getData: $name")
                        nameArray.add(name)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


}