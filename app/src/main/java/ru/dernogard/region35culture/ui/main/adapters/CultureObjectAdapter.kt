package ru.dernogard.region35culture.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.databinding.ItemCultureObjectBinding

const val BUNDLE_CULTURE_OBJECT = "cultureObject"

internal class CultureObjectAdapter :
    ListAdapter<CultureObject, CultureObjectAdapter.CultureObjectHolder>(CultureObjectDiffCallback()) {

    class CultureObjectDiffCallback : DiffUtil.ItemCallback<CultureObject>() {
        override fun areItemsTheSame(oldItem: CultureObject, newItem: CultureObject): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CultureObject, newItem: CultureObject): Boolean =
            oldItem.title == newItem.title
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CultureObjectHolder(
        ItemCultureObjectBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun onBindViewHolder(holder: CultureObjectAdapter.CultureObjectHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CultureObjectHolder(private val binding: ItemCultureObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cultureObject: CultureObject) {
            binding.cultureObject = cultureObject
            binding.executePendingBindings()

            binding.cardCultureObject.setOnClickListener { openDetailScreen(cultureObject) }
        }

        private fun openDetailScreen(cultureObject: CultureObject) {
            val bundle = bundleOf(BUNDLE_CULTURE_OBJECT to cultureObject)
            binding.root.findNavController()
                .navigate(R.id.action_cultureListFragment_to_cultureDetailFragment, bundle )
        }
    }
}
