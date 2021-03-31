package com.none.eunoia.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.none.eunoia.Model.Notification
import com.none.eunoia.Model.Post
import com.none.eunoia.Model.User
import com.none.eunoia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.posts_layout.view.*
import java.util.*

class PostAdapter(private val mContext: Context,
                  private val mPost:List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser?=null
    private var flag=1
    private var flag2=1
    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage:ImageView
        var likeButton:ImageView
        var commentButton :ImageView

        var donateButton:ImageView
        var userName:TextView
        var likes:TextView
        var publisher:TextView
        var description:TextView

init{
    profileImage=itemView.findViewById(R.id.user_profile_image_post)
    postImage=itemView.findViewById(R.id.post_image_home)
    likeButton=itemView.findViewById(R.id.post_image_like_btn)
    commentButton=itemView.findViewById(R.id.post_image_comment_btn)
    userName=itemView.findViewById(R.id.user_name_post)
    donateButton=itemView.findViewById(R.id.post_donate_button)
    likes=itemView.findViewById(R.id.likes)
    publisher=itemView.findViewById(R.id.publisher)
    description=itemView.findViewById(R.id.description)

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val post =mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        holder.description.text=post.getDescription()
        if(post.getPublisher()==firebaseUser?.uid)
        {
            holder.donateButton.visibility=View.INVISIBLE
        }
publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())
        isLiked(post.getPostid(),holder.likeButton)
        likeCount(post.getPostid(),holder.likes)
        isDonated(post.getPostid(),holder.donateButton)
        holder.donateButton.setOnClickListener {
            if(flag2==1)
            {
                firebaseUser?.uid.let{
                    FirebaseDatabase.getInstance().reference.child("Donations").child("Donater")
                        .child(it.toString()).child(post.getPostid()).child(post.getPublisher()).setValue(true).addOnCompleteListener {task->
                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let{
                                FirebaseDatabase.getInstance().reference.child("Donations").child("Reciever")
                                    .child(post.getPublisher()).child(post.getPostid()).child(it.toString()).setValue(true).addOnCompleteListener {task->
                                        if(task.isSuccessful){updateNotification(post.getPostid(),post.getPublisher())}
                                    }
                            }}
                        }
                }
            }
            else
            {
                firebaseUser?.uid.let{
                    FirebaseDatabase.getInstance().reference.child("Donations").child("Donater")
                        .child(it.toString()).child(post.getPostid()).removeValue().addOnCompleteListener { task->
                            if(task.isSuccessful)
                            {
                                firebaseUser?.uid.let{
                                    FirebaseDatabase.getInstance().reference.child("Donations").child("Reciever")
                                        .child(post.getPublisher()).child(post.getPostid()).child(it.toString()).removeValue().addOnCompleteListener {
                                            if(task.isSuccessful){}
                                        }
                                }
                            }
                        }
                }
            }
        }
holder.itemView.post_image_like_btn.setOnClickListener{
if(flag==1)
{
    firebaseUser?.uid.let{
        FirebaseDatabase.getInstance().reference.child("Likes")
            .child(post.getPostid()).child("likedBy")
            .child(it.toString()).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    removeNotification(post.getPostid(),post.getPublisher())
                }
            }
    }
}
    else
{
    firebaseUser?.uid.let{
        FirebaseDatabase.getInstance().reference.child("Likes")
            .child(post.getPostid()).child("likedBy").child(it.toString())
            .setValue(true).addOnCompleteListener { task->
            if(task.isSuccessful){
            }}
    }

}
}
    }

    private fun removeNotification(postid: String,publisher: String) {

        val ref=firebaseUser?.uid.let{FirebaseDatabase.getInstance().reference
            .child("Notifications").child(it.toString())}.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (snapshot in p0.children) {
                        val notification = snapshot.getValue(Notification::class.java)
                        if(notification!!.getPostid()==postid)
                        {
                            val ref1=FirebaseDatabase.getInstance().reference
                                .child("Notifications").child(firebaseUser!!.uid)
                                .child(notification.getNotificationId()).removeValue().addOnCompleteListener { task->
                                    if(task.isSuccessful)
                                    {
                                        FirebaseDatabase.getInstance().reference.child("Notifications")
                                            .child(publisher).child(notification.getNotificationId()).removeValue()
                                        Log.i("done","deleted")
                                    }
                                }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
            })
    }

    private fun updateNotification(postid: String, publisher: String) {
        val ref=FirebaseDatabase.getInstance().reference
            .child("Notifications")
        val notiMap=HashMap<String, Any>()
        val notiId=ref.push().key
        val r=10000..99999
        notiMap.put("notificationId",notiId!!)
        notiMap.put("postid",postid)
        notiMap.put("donator",firebaseUser!!.uid)
        notiMap.put("reciever",publisher)
        notiMap.put("status",0.toString())
        notiMap.put("key",r.random().toString())
        ref.child(firebaseUser!!.uid).child(notiId).updateChildren(notiMap)
        ref.child(publisher).child(notiId).updateChildren(notiMap)

    }

    private fun isDonated(postid: String, donateButton: ImageView) {
        val ref=firebaseUser?.uid.let{
            FirebaseDatabase.getInstance().reference.child("Donations").child("Donater")
                .child(it.toString())
        }.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(postid).exists())
                {flag2=0
                    donateButton.setImageResource(R.drawable.donated)
                }
                else{
                    flag2=1
                    donateButton.setImageResource(R.drawable.donation)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun likeCount(postid: String, likes: TextView) {
        val likedByRef=FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)
            .child("likedBy")
        likedByRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                likes.text=snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun isLiked(postid: String,likeButton:ImageView) {
val likedByRef= FirebaseDatabase.getInstance().reference
        .child("Likes").child(postid)
        .child("likedBy")

        likedByRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(FirebaseAuth.getInstance().currentUser.uid.toString()).exists())
                {
                    flag=1
                    likeButton.setImageResource(R.drawable.heart_clicked)
                }
                else
                {
                    flag=0

                 likeButton.setImageResource(R.drawable.heart_not_clicked)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    override fun getItemCount(): Int {
        return mPost.size
    }
    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {
val usersRef=FirebaseDatabase.getInstance().reference.child("Users")
    .child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
if(p0.exists())
{ val user=p0.getValue<User>(User::class.java)
    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
    userName.text=user!!.getUsername()
    publisher.text=user!!.getFullname()
} }
            override fun onCancelled(error: DatabaseError)
            {} })
    }


}