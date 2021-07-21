package ru.geekbrains.android2.nasapicture.view.notes

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolderNotes(itemView: View) :
    RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolderNotes {
    abstract fun bind(dataItem: Pair<Note, Boolean>)
}