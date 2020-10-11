package ru.dernogard.region35culture.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.databinding.ItemCultureGroupBinding
import ru.dernogard.region35culture.ui.main.fragments.CultureListFragment

/**
 * This adapter used for showing Culture Group above
 * @see CultureListFragment
 */

internal class CultureGroupsAdapter :
    ListAdapter<CultureGroup, CultureGroupsAdapter.CultureGroupHolder>(CultureGroupDiffCallback()) {

    lateinit var groupSelectedListener: GroupSelectedListener

    class CultureGroupDiffCallback : DiffUtil.ItemCallback<CultureGroup>() {
        override fun areItemsTheSame(oldItem: CultureGroup, newItem: CultureGroup): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CultureGroup, newItem: CultureGroup): Boolean =
            oldItem.title == newItem.title
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CultureGroupHolder(
        ItemCultureGroupBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun onBindViewHolder(holder: CultureGroupsAdapter.CultureGroupHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CultureGroupHolder(private val binding: ItemCultureGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: CultureGroup) {
            binding.textCultureGroup.text = group.title
            binding.cardCultureGroup.setOnClickListener { openCultureObjectListScreen(group) }
        }

        private fun openCultureObjectListScreen(group: CultureGroup) {
            groupSelectedListener.onGroupSelected(group)
        }
    }

    interface GroupSelectedListener {
        fun onGroupSelected(group: CultureGroup)
    }
}