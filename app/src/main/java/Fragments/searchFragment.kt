package Fragments

import android.os.Bundle
import android.renderscript.Sampler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.none.eunoia.Adapter.UserAdapter
import com.none.eunoia.Model.User
import com.none.eunoia.R
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [searchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchFragment : Fragment() {
    private var recyclerView: RecyclerView?=null
    private var userAdapter: UserAdapter?=null
    private var mUser: MutableList<User>?=null
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
        val view= inflater.inflate(R.layout.fragment_search, container, false)
recyclerView=view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)

        mUser=ArrayList()
        userAdapter=context?.let{
            UserAdapter(it,mUser as ArrayList<User>,true)
        }
        recyclerView?.adapter = userAdapter

        view.search_edit_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(view.search_edit_text.text.toString()=="")
                {

                }
                else
                {
                    recyclerView?.visibility=View.VISIBLE
                    retrieveUsers()
                    searchUser(s.toString())
                }
            }
        })


        return view
    }

    private fun searchUser(input: String) {
        val query= FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input+"\uf8ff")

            query.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        mUser?.clear()
                            for(snapshot in dataSnapshot.children)
                            {
                                val user=snapshot.getValue(User::class.java)
                                if(user!=null)
                                {
                                    mUser?.add(user)
                                }
                            }
                            userAdapter?.notifyDataSetChanged()


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

    }

    private fun retrieveUsers() {
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
if(view?.search_edit_text?.text.toString()=="")
    {
        mUser?.clear()
        for(snapshot in dataSnapshot.children)
        {
            val user=snapshot.getValue(User::class.java)
            if(user!=null)
            {
                mUser?.add(user)
            }
        }
        userAdapter?.notifyDataSetChanged()
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
         * @return A new instance of fragment searchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}