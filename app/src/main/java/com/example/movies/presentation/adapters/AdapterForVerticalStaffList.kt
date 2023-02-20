package com.example.movies.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.movies.R
import com.example.movies.databinding.ItemForVerticalStaffListBinding

class AdapterForVerticalStaffList(
    private var list: List<StaffRelatedToMovie>
) : RecyclerView.Adapter<ViewHolderForVerticalStaffList>() {

    private var actionOnItemClick: ((StaffRelatedToMovie) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForVerticalStaffList {

        val binding = ItemForVerticalStaffListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForVerticalStaffList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForVerticalStaffList, position: Int) {
        val staff = list[position]
        (holder.binding).apply {
            Glide
                .with(image)
                .load(staff.posterUrl)
                .centerCrop()
                .into(image)
            staffName.text = defineName(staff)
            if (staff.professionKey == StaffRelatedToMovie.ACTOR) {
                role.text = staff.description
            } else {
                role.text = when (staff.professionKey) {
                    StaffRelatedToMovie.WRITER ->
                        holder.binding.root.context.resources.getString(R.string.writer)
                    StaffRelatedToMovie.OPERATOR ->
                        holder.binding.root.context.resources.getString(R.string.operator)
                    StaffRelatedToMovie.EDITOR ->
                        holder.binding.root.context.resources.getString(R.string.editor)
                    StaffRelatedToMovie.COMPOSER ->
                        holder.binding.root.context.resources.getString(R.string.composer)
                    StaffRelatedToMovie.PRODUCER_USSR ->
                        holder.binding.root.context.resources.getString(R.string.producer_ussr)
                    StaffRelatedToMovie.TRANSLATOR ->
                        holder.binding.root.context.resources.getString(R.string.translator)
                    StaffRelatedToMovie.DIRECTOR ->
                        holder.binding.root.context.resources.getString(R.string.director)
                    StaffRelatedToMovie.DESIGN ->
                        holder.binding.root.context.resources.getString(R.string.design)
                    StaffRelatedToMovie.PRODUCER ->
                        holder.binding.root.context.resources.getString(R.string.producer)
                    StaffRelatedToMovie.ACTOR ->
                        holder.binding.root.context.resources.getString(R.string.actor)
                    StaffRelatedToMovie.VOICE_DIRECTOR ->
                        holder.binding.root.context.resources.getString(R.string.voice_director)
                    StaffRelatedToMovie.UNKNOWN ->
                        holder.binding.root.context.resources.getString(R.string.unknown)
                    else -> throw IllegalStateException()
                }
            }
            root.setOnClickListener {
                actionOnItemClick?.let {
                    it(staff)
                }
            }
        }
    }

    private fun defineName(staff: StaffRelatedToMovie): String {
        return if (staff.nameRu != null &&
            staff.nameRu.toString().isNotBlank()
        ) {
            staff.nameRu!!
        } else {
            staff.nameEn ?: ""
        }
    }

    override fun getItemCount(): Int = list.size

    fun setItemClickListener(action: (StaffRelatedToMovie) -> Unit) {
        actionOnItemClick = action
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<StaffRelatedToMovie>){
        list = newList
        notifyDataSetChanged()
    }
}

class ViewHolderForVerticalStaffList(val binding: ItemForVerticalStaffListBinding) :
    ViewHolder(binding.root)