package Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationAttributes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.none.eunoia.AccountSettingsActivity
import com.none.eunoia.Adapter.PostAdapter
import com.none.eunoia.Model.Post
import com.none.eunoia.Model.User
import com.none.eunoia.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment() {
    private var postAdapter: PostAdapter?=null
    private var postList:MutableList<Post>?=null
    private var donationList: MutableList<Post>?=null


    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
            val pref=context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        firebaseUser= FirebaseAuth.getInstance().currentUser!!
        if(pref!=null)
        {
            this.profileId= pref.getString("profileId","none")!!
        }
        if(profileId==firebaseUser.uid)
        {
            view.edit_account_settings_account_btn.text="Edit Profile"
        }
else if(profileId!=firebaseUser.uid)
        {
    checkFollowAndFollowingButtonStatus()
        }

            // Inflate the layout for this fragment
            var recyclerView: RecyclerView?=null
            recyclerView=view.findViewById(R.id.recyler_view_profile)
            val linearLayoutManager=LinearLayoutManager(context)
            linearLayoutManager.reverseLayout=true
            linearLayoutManager.stackFromEnd=true
            recyclerView.layoutManager=linearLayoutManager
            postList= ArrayList()
            postAdapter=context?.let{
                PostAdapter(it,postList as ArrayList<Post>)
            }
            recyclerView.adapter=postAdapter
retrievePost()


        view.edit_account_settings_account_btn.setOnClickListener {
            val getButtonText=view.edit_account_settings_account_btn.text.toString()
            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountSettingsActivity::class.java))
                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                                           }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }
                getButtonText=="Following" ->{
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }


            }
        }

        getFollowers()
        getFollowings()
        getDonations()
        userInfo()
        view.images_save_btn.setOnClickListener{
            getdonationList()
        }
        view.images_grid_view_btn.setOnClickListener {
            retrievePost()
        }
        return view
    }

    private fun getdonationList()     {
        donationList=ArrayList()
        val donationRef= FirebaseDatabase.getInstance().reference
            .child("Users").child(profileId)
            .child("donations")
        donationRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    (donationList as ArrayList<String>).clear()
                    for (snapshot in p0.children)
                    {
                        snapshot.key?.let{(donationList as ArrayList<String>).add(it)}
                    }
                    donatedPosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun donatedPosts() {
        val postsRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()
                for(snapshot in p0.children)
                {
                    val post=snapshot.getValue(Post::class.java)

                    for(id in (donationList as ArrayList<String>))
                    {
                        if(post!!.getPostid()==id.toString())
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun retrievePost() {
        val postsRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()
                for(snapshot in p0.children)
                {
                    val post=snapshot.getValue(Post::class.java)

                        if(post!!.getPublisher()==profileId)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }




    private fun getDonations() {
        val userRef=FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileId).child("donations")
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    view?.total_donations!!.text=snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
    if(followingRef!=null)
    {
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(profileId).exists())
                {
                    view?.edit_account_settings_account_btn?.text="Following"
                }
                else
                {
                    view?.edit_account_settings_account_btn?.text="Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getFollowers()
    {
        val followersRef=
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")
        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
if(p0.exists())
{
    view?.total_followers?.text=p0.childrenCount.toString()
}
                else
{
    view?.total_followers?.text=p0.childrenCount.toString()
}
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getFollowings()
    {
        val followersRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")
        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    view?.total_following?.text=(p0.childrenCount-1).toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun userInfo()
    {
        val usersRef=FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists())
                {
                    val user=p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text=user!!.getUsername()
                    view?.full_name_profile_frag?.text=user!!.getFullname()
                    view?.bio_profile_frag?.text=user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()


    }

    override fun onDestroy() {
        super.onDestroy()
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()



    }
}