package com.mimi.destinationfinder.settings

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.destinationfinder.R
import kotlinx.android.synthetic.main.item_place_type.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class PlaceTypeAdapter(private val allItems:List<String>,
                       private val selectedItems:ArrayList<String> = arrayListOf(),
                       val context:Context): RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount() = allItems.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
     = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_place_type,parent,false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val name = allItems[position]
        holder?.bindItem(name,selectedItems.contains(name)){
            if(selectedItems.contains(name))
                selectedItems.remove(name)
            else
                selectedItems.add(name)
            notifyDataSetChanged()
        }

    }

}
class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bindItem(name:String, isSelected:Boolean, onClick:(Int)->Unit){
        with(itemView.label){
            text = name
            if(isSelected){
                textColor = ContextCompat.getColor(context, android.R.color.white)
                backgroundColor = ContextCompat.getColor(context,R.color.colorPrimary)
            } else {
                textColor = ContextCompat.getColor(context, R.color.colorPrimary)
                backgroundColor = ContextCompat.getColor(context,android.R.color.white)

            }
            setOnClickListener { onClick(adapterPosition) }
        }
    }
}
