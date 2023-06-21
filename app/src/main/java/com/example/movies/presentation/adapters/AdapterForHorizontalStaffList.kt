package com.example.movies.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.movies.R
import com.example.movies.databinding.ItemForHorizontalStaffListBinding


class AdapterForHorizontalStaffList(
    private val currentLocale: String,
    private val actionOnItemClick: (StaffRelatedToMovie) -> Unit
) : RecyclerView.Adapter<ViewHolderForHorizontalStaffList>() {

    private var maxStaffListSize = 20 // default

    private var list: List<StaffRelatedToMovie> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForHorizontalStaffList {
        val binding = ItemForHorizontalStaffListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForHorizontalStaffList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForHorizontalStaffList, position: Int) {
        val staff = list[position]
        holder.binding.apply {
            Glide
                .with(personImage)
                .load(staff.posterUrl)
                .placeholder(R.drawable.ic_profile)
                .into(personImage)
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
                actionOnItemClick(staff)
            }
        }
    }

    private fun defineName(staff: StaffRelatedToMovie): String {
        return if (currentLocale == "RU" && staff.nameRu != null &&
            staff.nameRu.toString().isNotBlank()
        ) {
            staff.nameRu!!
        } else if (staff.nameEn != null &&
            staff.nameEn.toString().isNotBlank()
        ) {
            staff.nameEn!!
        } else if (staff.nameRu != null &&
            staff.nameRu.toString().isNotBlank()
        ) {
            staff.nameRu!!
        } else {
            ""
        }
    }

    override fun getItemCount(): Int {
        return if (list.size <= maxStaffListSize) list.size
        else maxStaffListSize
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewList(newList: List<StaffRelatedToMovie>) {
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMaxStaffListSize(listSize: Int) {
        maxStaffListSize = listSize
        notifyDataSetChanged()
    }
}

class ViewHolderForHorizontalStaffList(val binding: ItemForHorizontalStaffListBinding) :
    ViewHolder(binding.root)