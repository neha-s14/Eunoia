package com.none.eunoia.Adapter

import android.content.Context
import android.media.Image
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.none.eunoia.Model.Notification
import com.none.eunoia.Model.User
import com.none.eunoia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.notifications_layout.view.*
import org.w3c.dom.Text

class NotificationAdapter(private val mContext:Context,
private val mNotifications:List<Notification>):RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){
    private var firebaseUser: FirebaseUser?=null
    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var profileImage:CircleImageView
        var username:TextView
        var message:TextView
        var acceptButton:Button
        var emailid:TextView
        var key:TextView
        var keyEnter:EditText
        var keyConfirmBtn:Button
        init{
            profileImage=itemView.findViewById(R.id.user_profile_image_notifications)
            username=itemView.findViewById(R.id.user_name_notifications)
            message=itemView.findViewById(R.id.message_notifications)
            acceptButton=itemView.findViewById(R.id.accept_btn)
            emailid=itemView.findViewById(R.id.email_notification)
            key=itemView.findViewById(R.id.key_notifications)
            keyEnter=itemView.findViewById(R.id.key_enter_notifications)
            keyConfirmBtn=itemView.findViewById(R.id.key_confirm_btn)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val notification=mNotifications[position]

        if(notification.getDonator()==firebaseUser?.uid.toString()) {
            holder.acceptButton.visibility=View.INVISIBLE
            holder.key.visibility=View.INVISIBLE
            picAndUsername(holder.username, holder.profileImage, notification.getReciever(),holder.emailid)
            if(notification.getStatus().toString()=="0")
            {
            holder.message.text="has not accepted your donation request yet"
            holder.emailid.visibility=View.INVISIBLE
                holder.keyEnter.visibility=View.INVISIBLE
                holder.keyConfirmBtn.visibility=View.INVISIBLE
            }
            else
            {
                holder.message.text="has accepted your donation request "
                holder.emailid.visibility=View.VISIBLE
                holder.keyEnter.visibility=View.VISIBLE
                holder.keyConfirmBtn.visibility=View.VISIBLE
            }

        }
        else if(notification.getReciever()==firebaseUser?.uid.toString())
        {
            holder.keyEnter.visibility=View.INVISIBLE
            holder.keyConfirmBtn.visibility=View.INVISIBLE
            picAndUsername(holder.username, holder.profileImage, notification.getDonator(),holder.emailid)
            holder.acceptButton.setOnClickListener {
                val notificationRef=FirebaseDatabase.getInstance().reference.child("Notifications")

                val notificationMap=HashMap<String,Any>()
                notificationMap.put("status","1")
                notificationRef.child(firebaseUser?.uid.toString()).child(notification.getNotificationId()).updateChildren(notificationMap)
                notificationRef.child(notification.getDonator()).child(notification.getNotificationId()).updateChildren(notificationMap)

            }

if(notification.getStatus().toString()=="0")
{
    holder.acceptButton.text="Accept"
    holder.key.visibility=View.INVISIBLE
    holder.message.text="has requested to donate"
    holder.emailid.visibility=View.INVISIBLE

}
            else
{
    holder.acceptButton.text="Accepted"
    holder.key.visibility=View.VISIBLE
    holder.key.text="Unique Code to verify completion of donation: ${notification.getKey()}"
    holder.message.text="You have accepted the donation request"
    holder.emailid.visibility=View.VISIBLE

}
        }
holder.keyConfirmBtn.setOnClickListener {
    when {
        TextUtils.isEmpty(holder.keyEnter.toString())->{
            Toast.makeText(mContext,"Enter a key",Toast.LENGTH_SHORT).show()
        }
        holder.keyEnter.text.toString()!=notification.getKey()->
        {
            Toast.makeText(mContext,"Enter a valid key",Toast.LENGTH_SHORT).show()
        }
        else->
        {
            val userRef=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                .child("donations").child(notification.getPostid()).setValue(true).addOnCompleteListener { task->
                    if(task.isSuccessful)
                    {
                        holder.keyConfirmBtn.text="Confirmed"
                        holder.keyConfirmBtn.isClickable=false
                    }
                }
        }
    }
}
    }

    private fun picAndUsername(username: TextView, profileImage: CircleImageView, userid: String,emailid:TextView) {
        val usersRef= FirebaseDatabase.getInstance().reference.child("Users")
            .child(userid)
        usersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)
                Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                username.text=user.getUsername()
                emailid.text="Contact ${user.getFullname()} at ${user.getEmail()}"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun getItemCount(): Int {
return mNotifications.size    }

}