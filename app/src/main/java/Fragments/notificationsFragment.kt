package Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.none.eunoia.Adapter.NotificationAdapter
import com.none.eunoia.Adapter.PostAdapter
import com.none.eunoia.Model.Notification
import com.none.eunoia.Model.Post
import com.none.eunoia.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [notificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class notificationsFragment : Fragment() {
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<Notification>? = null

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
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        notificationList = ArrayList()
        notificationList = java.util.ArrayList()
        notificationAdapter = context?.let {
            NotificationAdapter(it, notificationList as java.util.ArrayList<Notification>)
        }
        recyclerView.adapter=notificationAdapter
        retrieveNotifications()
            return view

    }

    private fun retrieveNotifications() {
        val ref = FirebaseAuth.getInstance().currentUser.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Notifications").child(it.toString())
        }.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                notificationList?.clear()
                for(snapshot in p0.children )
                {
                    val notification=snapshot.getValue(Notification::class.java)
                    notificationList!!.add(notification!!)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment notificationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            notificationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}