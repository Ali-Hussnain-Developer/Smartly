package com.example.smartly.presentation.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class GenericRecyclerViewAdapter<T, VB : ViewDataBinding>(
    private val context: Context,
    private var items: List<T>,
    private val layoutId: Int,
    private val bindVariableId: Int,
    private val onItemClick: (item: T) -> Unit
) : RecyclerView.Adapter<GenericRecyclerViewAdapter.GenericViewHolder<VB>>() {

    class GenericViewHolder<VB : ViewDataBinding>(
        val binding: VB
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<VB>(inflater, layoutId, parent, false)
        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<VB>, position: Int) {
        val item = items[position]
        holder.binding.setVariable(bindVariableId, item)
        holder.binding.executePendingBindings()

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }
}

