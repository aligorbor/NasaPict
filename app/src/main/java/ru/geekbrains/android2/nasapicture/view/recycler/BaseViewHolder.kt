package ru.geekbrains.android2.nasapicture.view.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),ItemTouchHelperViewHolder {
    abstract fun bind(data: Pair<Data, Boolean>)
}