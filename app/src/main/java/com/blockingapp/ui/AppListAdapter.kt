package com.blockingapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blockingapp.databinding.ItemAppBinding

class AppListAdapter(
    private val onToggle: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppListAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(app: AppInfo) {
            binding.ivAppIcon.setImageDrawable(app.icon)
            binding.tvAppName.text = app.name
            binding.tvPackageName.text = app.packageName
            binding.cbBlocked.isChecked = app.isBlocked
            binding.tvPendingRemoval.visibility =
                if (app.isPendingRemoval) android.view.View.VISIBLE else android.view.View.GONE
            binding.tvFeatureBlocked.text =
                com.blockingapp.FeatureBlocks.forPackage(app.packageName)?.summary
                    ?: "🚫 ${app.blockedFeature} blocked · rest of app works"
            binding.tvFeatureBlocked.visibility =
                if (app.blockedFeature != null && !app.isPendingRemoval) android.view.View.VISIBLE
                else android.view.View.GONE

            // Prevent the checkbox from consuming the click before the row does.
            binding.cbBlocked.isClickable = false
            binding.root.setOnClickListener { onToggle(app) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppInfo>() {
            override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
                oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
                oldItem == newItem
        }
    }
}
