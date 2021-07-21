package ru.geekbrains.android2.nasapicture.view.notes

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.activity_notes_item.view.*
import kotlinx.android.synthetic.main.activity_notes_item_header.view.*
import ru.geekbrains.android2.nasapicture.R
import kotlin.math.abs

private const val priorityMax = 3

class NotesActivity : AppCompatActivity() {

    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        val data = arrayListOf(
            Pair(Note(), false),
        )
        data.add(0, Pair(Note("Header"), false))
        val adapter = NotesActivityAdapter(
            object : NotesActivityAdapter.OnListItemClickListener {
                override fun onItemClick(data: Note) {
                    Toast.makeText(
                        this@NotesActivity, data.noteHeader,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            data,
            object : NotesActivityAdapter.OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            }
        )
        recyclerViewNotes.adapter = adapter
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(this,
//            LinearLayoutManager.VERTICAL)
//        )
        recyclerActivityFABNotes.setOnClickListener { adapter.appendItem() }
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallbackNotes(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes)
    }
}

class NotesActivityAdapter(
    private val onListItemClickListener: OnListItemClickListener,
    private var data: MutableList<Pair<Note, Boolean>>,
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<BaseViewHolderNotes>(), ItemTouchHelperAdapterNotes {

    private var priorityFilter = 0
    private var dataOriginal: MutableList<Pair<Note, Boolean>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseViewHolderNotes {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_NOTE ->
                NotesViewHolder(
                    inflater.inflate(
                        R.layout.activity_notes_item,
                        parent, false
                    ) as View
                )
            else -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.activity_notes_item_header, parent,
                    false
                ) as View
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolderNotes, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_HEADER
            else -> TYPE_NOTE
        }
    }

    fun appendItem() {
        data.add(generateItem())
        notifyDataSetChanged()
    }

    private fun generateItem() = Pair(Note(notePriority = priorityFilter), false)

    inner class NotesViewHolder(view: View) : BaseViewHolderNotes(view) {
        override fun bind(dataItem: Pair<Note, Boolean>) {

            when (dataItem.first.notePriority) {
                1 -> itemView.noteImageViewPriority.setImageResource(R.drawable.bg_earth)
                2 -> itemView.noteImageViewPriority.setImageResource(R.drawable.bg_mars)
                3 -> itemView.noteImageViewPriority.setImageResource(R.drawable.bg_system)
                else -> {
                    itemView.noteImageViewPriority.setImageResource(R.drawable.indicator_priority)
                }
            }
            itemView.noteImageViewPriority.setOnClickListener {
                onListItemClickListener.onItemClick(dataItem.first)
         //       if (++dataItem.first.notePriority > priorityMax) dataItem.first.notePriority = 0
                if (++data[layoutPosition].first.notePriority > priorityMax) data[layoutPosition].first.notePriority = 0

                notifyItemChanged(layoutPosition)
            }
            itemView.addItemImageViewNote.setOnClickListener { addItem() }
            itemView.removeItemImageViewNote.setOnClickListener { removeItem() }
            itemView.moveItemDownNote.setOnClickListener { moveDown() }
            itemView.moveItemUpNote.setOnClickListener { moveUp() }

            itemView.noteDescriptionTextView.visibility =
                if (dataItem.second) View.VISIBLE else View.GONE
            itemView.noteTextView.setOnClickListener { toggleText() }
            itemView.dragHandleImageViewNote.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) ==
                    MotionEvent.ACTION_DOWN
                ) {
                    dragListener.onStartDrag(this)
                }
                false
            }

            itemView.noteTextView.setText(dataItem.first.noteHeader)
            itemView.noteDescriptionTextView.setText(dataItem.first.noteText)

            itemView.noteTextView.doOnTextChanged { text, _, _, _ ->
              //  dataItem.first.noteHeader = text.toString()
                data[layoutPosition].first.noteHeader = text.toString()
            }
            itemView.noteDescriptionTextView.doOnTextChanged { text, _, _, _ ->
            //    dataItem.first.noteText = text.toString()
                data[layoutPosition].first.noteText = text.toString()
            }
        }

        private fun addItem() {
            data.add(layoutPosition, generateItem())
            notifyItemInserted(layoutPosition)
        }

        private fun removeItem() {
            data.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition - 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            //      data[layoutPosition] = Pair(data[layoutPosition].first, !data[layoutPosition].second)
            notifyItemChanged(layoutPosition)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class HeaderViewHolder(view: View) : BaseViewHolderNotes(view) {
        override fun bind(dataItem: Pair<Note, Boolean>) {
            when (priorityFilter) {
                1 -> itemView.notePriorityImageViewHeader.setImageResource(R.drawable.bg_earth)
                2 -> itemView.notePriorityImageViewHeader.setImageResource(R.drawable.bg_mars)
                3 -> itemView.notePriorityImageViewHeader.setImageResource(R.drawable.bg_system)
                else -> {
                    itemView.notePriorityImageViewHeader.setImageResource(R.drawable.indicator_priority)
                }
            }

            itemView.notePriorityImageViewHeader.setOnClickListener {
                data.removeAt(0)
                dataOriginal.addAll(data)
                data.clear()
                data.addAll(dataOriginal)
                if (++priorityFilter > priorityMax) priorityFilter = 0
                if (priorityFilter > 0) {
                    data.retainAll { it.first.notePriority == priorityFilter }
                    data.add(0, Pair(Note("Header"), false))
                    dataOriginal.removeAll { it.first.notePriority == priorityFilter }
                } else {
                    data.add(0, Pair(Note("Header"), false))
                    dataOriginal.clear()
                }
                notifyDataSetChanged()
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    interface OnListItemClickListener {
        fun onItemClick(data: Note)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(
                if (toPosition > fromPosition) toPosition - 1 else
                    toPosition, this
            )
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    companion object {
        private const val TYPE_NOTE = 1
        private const val TYPE_HEADER = 2
    }

}

interface ItemTouchHelperAdapterNotes {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolderNotes {
    fun onItemSelected()
    fun onItemClear()
}

class ItemTouchHelperCallbackNotes(private val adapter: NotesActivityAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolderNotes
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolderNotes
        itemViewHolder.onItemClear()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width.toFloat()
            val alpha = 1.0f - abs(dX) / width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }
}