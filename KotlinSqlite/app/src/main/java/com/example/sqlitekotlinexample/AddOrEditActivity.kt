package com.example.sqlitekotlinexample

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.sqlitekotlinexample.databinding.ActivityAddEditBinding
import com.example.sqlitekotlinexample.db.DatabaseHandler
import com.example.sqlitekotlinexample.models.Tasks

class AddOrEditActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var isEditMode = false

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerPriorityAdapter: ArrayAdapter<String>
    private val priority = arrayOf("Low", "Normal", "High")
    private var selectedPriority = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //get the spinner reference
        spinnerPriority = binding.spinner
        spinnerPriorityAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priority
        )
        spinnerPriority.adapter = spinnerPriorityAdapter
        spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedPriority = priority[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        initDB()
        initOperations()
    }

    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        binding.btnDelete.visibility = View.INVISIBLE
        if (intent != null && intent.getStringExtra("Mode") == "Edit") {
            isEditMode = true
            val tasks: Tasks = dbHandler!!.getTask(intent.getIntExtra("Id", 0))
            binding.inputName.setText(tasks.name)
            binding.inputDesc.setText(tasks.desc)
            binding.spinner.setSelection(spinnerPriorityAdapter.getPosition(tasks.priority))
            binding.swtCompleted.isChecked = tasks.completed == "Y"
            binding.btnDelete.visibility = View.VISIBLE
        }
    }

    private fun initOperations() {
        binding.btnSave.setOnClickListener {
            var success: Boolean = false
            if (!isEditMode) {
                val tasks: Tasks = Tasks()
                tasks.name = binding.inputName.text.toString()
                tasks.desc = binding.inputDesc.text.toString()
                tasks.priority = selectedPriority
                if (binding.swtCompleted.isChecked)
                    tasks.completed = "Y"
                else
                    tasks.completed = "N"
                success = dbHandler?.addTask(tasks) as Boolean
            } else {
                val tasks: Tasks = Tasks()
                tasks.id = intent.getIntExtra("Id", 0)
                tasks.name = binding.inputName.text.toString()
                tasks.desc = binding.inputDesc.text.toString()
                tasks.priority = selectedPriority
                if (binding.swtCompleted.isChecked)
                    tasks.completed = "Y"
                else
                    tasks.completed = "N"
                success = dbHandler?.updateTask(tasks) as Boolean
            }

            if (success)
                finish()
        }

        binding.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(this).setTitle("Info")
                .setMessage("Click 'YES' Delete the Task.")
                .setPositiveButton("YES") { dialog, i ->
                    val success = dbHandler?.deleteTask(intent.getIntExtra("Id", 0)) as Boolean
                    if (success)
                        finish()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, i ->
                    dialog.dismiss()
                }
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
