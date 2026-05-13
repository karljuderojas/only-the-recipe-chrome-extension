package com.onlytherecipe

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Sectioned grocery list — items grouped by their source recipe. "Other"
// bucket at top for manually-added or pre-migration items. Tap header to
// collapse/expand; tap item to toggle done; long-press to delete.
sealed class GroceryRow {
    abstract val key: String

    data class Header(
        override val key: String,
        val name: String,
        val total: Int,
        val checked: Int,
        val expanded: Boolean
    ) : GroceryRow()

    data class Item(
        val item: GroceryItem
    ) : GroceryRow() {
        override val key = "item:${item.id}"
    }
}

class GroceryAdapter(
    private val onToggle: (GroceryItem) -> Unit,
    private val onDelete: (GroceryItem) -> Unit
) : ListAdapter<GroceryRow, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM   = 1

        // Sentinel for the "Other" bucket — distinct from any user recipe name.
        const val OTHER_KEY = " __other__"

        val DIFF = object : DiffUtil.ItemCallback<GroceryRow>() {
            override fun areItemsTheSame(a: GroceryRow, b: GroceryRow) = a.key == b.key
            override fun areContentsTheSame(a: GroceryRow, b: GroceryRow) = a == b
        }
    }

    private val collapsedKeys = mutableSetOf<String>()
    private var lastItems = emptyList<GroceryItem>()

    fun submitItems(items: List<GroceryItem>) {
        lastItems = items
        rebuild()
    }

    private fun toggleGroup(key: String) {
        if (collapsedKeys.contains(key)) collapsedKeys.remove(key) else collapsedKeys.add(key)
        rebuild()
    }

    private fun rebuild() {
        submitList(buildRows(lastItems, collapsedKeys))
    }

    private fun buildRows(items: List<GroceryItem>, collapsed: Set<String>): List<GroceryRow> {
        if (items.isEmpty()) return emptyList()

        val groups = items.groupBy { it.recipeName.ifBlank { OTHER_KEY } }
        val orderedKeys = mutableListOf<String>()
        if (groups.containsKey(OTHER_KEY)) orderedKeys.add(OTHER_KEY)
        val recipeKeys = groups.keys.filter { it != OTHER_KEY }
            .sortedByDescending { key -> groups[key]?.maxOfOrNull { it.id } ?: 0L }
        orderedKeys.addAll(recipeKeys)

        val rows = mutableListOf<GroceryRow>()
        orderedKeys.forEach { key ->
            val groupItems = groups[key] ?: return@forEach
            val displayName = if (key == OTHER_KEY) "Other" else key
            val expanded = !collapsed.contains(key)
            rows.add(GroceryRow.Header(
                key      = key,
                name     = displayName,
                total    = groupItems.size,
                checked  = groupItems.count { it.checked },
                expanded = expanded
            ))
            if (expanded) {
                groupItems.sortedBy { it.id }.forEach { rows.add(GroceryRow.Item(it)) }
            }
        }
        return rows
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroceryRow.Header -> TYPE_HEADER
        is GroceryRow.Item   -> TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderHolder(inflater.inflate(R.layout.item_grocery_header, parent, false))
            TYPE_ITEM   -> ItemHolder(inflater.inflate(R.layout.item_grocery, parent, false))
            else -> throw IllegalStateException("unexpected viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is GroceryRow.Header -> (holder as HeaderHolder).bind(row)
            is GroceryRow.Item   -> (holder as ItemHolder).bind(row.item)
        }
    }

    inner class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView     = view.findViewById(R.id.groupName)
        private val count: TextView    = view.findViewById(R.id.groupCount)
        private val chevron: ImageView = view.findViewById(R.id.groupChevron)

        fun bind(header: GroceryRow.Header) {
            name.text = header.name
            val remaining = header.total - header.checked
            count.text = if (remaining > 0) "$remaining of ${header.total}" else "all done"
            chevron.setImageResource(
                if (header.expanded) R.drawable.ic_otr_chevron_down
                else R.drawable.ic_otr_chevron_right
            )
            itemView.setOnClickListener { toggleGroup(header.key) }
        }
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val divider: View   = view.findViewById(R.id.itemDivider)
        private val check: CheckBox = view.findViewById(R.id.groceryCheck)
        private val text: TextView  = view.findViewById(R.id.groceryText)

        init {
            // Dashed strokes need a software layer.
            divider.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        fun bind(item: GroceryItem) {
            text.text = item.text
            check.setOnCheckedChangeListener(null)
            check.isChecked = item.checked

            itemView.alpha = if (item.checked) 0.45f else 1f
            text.paintFlags = if (item.checked)
                text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            check.setOnClickListener { onToggle(item) }
            itemView.setOnClickListener {
                // Tapping the row (not the checkbox) toggles too — bigger target.
                check.toggle()
                onToggle(item)
            }
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context)
                    .setMessage("Remove \"${item.text}\"?")
                    .setPositiveButton("Remove") { _, _ -> onDelete(item) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }
}
