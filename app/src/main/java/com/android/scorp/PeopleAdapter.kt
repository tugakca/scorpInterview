package com.android.scorp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PeopleAdapter() : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    private var peopleList = mutableListOf<String>()

    class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val personName = view.findViewById(R.id.personTv) as TextView
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): PeopleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_people, parent, false)
        return PeopleViewHolder(view)

    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.personName.text = peopleList.get(position)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    fun addToList(peopleList: MutableList<String>) {
        this.peopleList.addAll(peopleList)
        this.peopleList = this.peopleList.distinct().toMutableList()
        notifyDataSetChanged()
    }

    fun clearList() {
        this.peopleList.clear()
        notifyDataSetChanged()
    }

}