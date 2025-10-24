package com.consultamedica.ui.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.consultamedica.R
import com.consultamedica.ui.adapter.SpecialistAdapter
import com.consultamedica.viewmodel.SpecialistViewModel

class SpecialistListActivity : AppCompatActivity() {

    private val specialistViewModel: SpecialistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_list)

        val rvSpecialists = findViewById<RecyclerView>(R.id.rvSpecialists)
        val adapter = SpecialistAdapter(specialistViewModel.specialists)

        rvSpecialists.layoutManager = LinearLayoutManager(this)
        rvSpecialists.adapter = adapter
    }
}
