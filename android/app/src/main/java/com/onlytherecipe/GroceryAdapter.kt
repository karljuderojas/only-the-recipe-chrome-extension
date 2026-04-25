package com.onlytherecipe

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GroceryAdapter(
    private val onToggle: (GroceryItem) -> Unit,
    private val onDelete: (GroceryItem) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBox = view.findViewById(R.id.groceryCheck)

        fun bind(item: GroceryItem) {
            checkBox.setOnCheckedChangeListener(null)
            checkBox.text = item.text
            checkBox.isChecked = item.checked
            checkBox.paintFlags = if (item.checked) {
                checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            checkBox.setOnCheckedChangeListener { _, _ -> onToggle(item) }
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context)
                    .setMessage("Remove \"${item.text}\" from list?")
                    .setPositiveButton("Remove") { _, _ -> onDelete(item) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<GroceryItem>() {
            override fun areItemsTheSame(a: GroceryItem, b: GroceryItem) = a.id == b.id
            override fun areContentsTheSame(a: GroceryItem, b: GroceryItem) = a == b
        }
    }
}
