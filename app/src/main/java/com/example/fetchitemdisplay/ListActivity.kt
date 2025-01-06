package com.example.fetchitemdisplay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchitemdisplay.databinding.ActivityListBinding
import com.example.fetchitemdisplay.models.DisplayItem
import com.example.fetchitemdisplay.viewmodels.ListViewModel

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var listViewModel: ListViewModel

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = binding.listRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        val filterFAB = binding.filterFab

        listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        listViewModel.displayItems.observe(this, { displayItems ->
            recyclerView.adapter = ListItemAdapter(this, displayItems)
        })
        listViewModel.withNullsAndEmptyStrings.observe(this, { withNullsAndEmptyStrings ->
            filterFAB.setImageResource(if (withNullsAndEmptyStrings) R.drawable.filter_white else R.drawable.filter_black)
        })

        listViewModel.retrieveData()

        filterFAB.setOnClickListener {
            listViewModel.toggleWithNullsAndEmptyStrings()
        }
    }

    class ListItemAdapter(private val context: Context, private val data: ArrayList<DisplayItem>) :
        RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idTextView: TextView = view.findViewById(R.id.item_id)
            val nameTextView: TextView = view.findViewById(R.id.item_name)
            val listIdTextView: TextView = view.findViewById(R.id.item_list_id)
            val listIdLabelTextView: TextView = view.findViewById(R.id.item_list_id_label)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_layout, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val itemRow = data[position]
            // if the row is an item row, then display the id and the name
            // else the row is a listId row, so display the listId
            if (itemRow.item != null) {
                viewHolder.idTextView.text = context.getString(R.string.item_id_label, itemRow.item.id.toString())
                viewHolder.nameTextView.text = context.getString(R.string.item_name_label, itemRow.item.name.toString())

                viewHolder.idTextView.visibility = View.VISIBLE
                viewHolder.nameTextView.visibility = View.VISIBLE
                viewHolder.listIdTextView.visibility = View.GONE
                viewHolder.listIdLabelTextView.visibility = View.GONE
            }
            else {
                viewHolder.listIdTextView.text = itemRow.listId.toString()

                viewHolder.idTextView.visibility = View.GONE
                viewHolder.nameTextView.visibility = View.GONE
                viewHolder.listIdTextView.visibility = View.VISIBLE
                viewHolder.listIdLabelTextView.visibility = View.VISIBLE
            }
        }

        override fun getItemCount() = data.size
    }
}