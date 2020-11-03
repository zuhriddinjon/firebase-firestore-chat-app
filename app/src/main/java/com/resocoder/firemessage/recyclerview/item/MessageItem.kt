package com.resocoder.firemessage.recyclerview.item

import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.resocoder.firemessage.R
import com.resocoder.firemessage.model.Message
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat


abstract class MessageItem(private val message: Message)
    : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }

    private fun setTimeText(viewHolder: GroupieViewHolder) {
        val dateFormat = SimpleDateFormat
                .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewHolder: GroupieViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_white)
                val lParams = FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.END)
                this.layoutParams = lParams
            }
        } else {
            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_primary_color)
                val lParams = FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }
}