package br.project_advhevogoober_final

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.Model.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_config.*
import kotlinx.android.synthetic.main.fragment_config.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.imperiumlabs.geofirestore.extension.getLocation

class ConfigFragment:Fragment() {

    val TAG ="ConfigFragment"
    val user = FirebaseAuth.getInstance().currentUser!!
    val db = FirebaseFirestore.getInstance()

    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view: View =inflater!!.inflate(R.layout.fragment_config, container,false)

        view.save_configs.setOnClickListener {
            saveConfig()
        }

        return view
    }

    fun saveConfig() {
        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val range:Double = (mapsRange.progress / 10).toDouble()
                val jurisdictions = listOf(
                    jurisdiction1.isChecked,
                    jurisdiction2.isChecked,
                    jurisdiction3.isChecked,
                    jurisdiction4.isChecked,
                    jurisdiction5.isChecked,
                    jurisdiction6.isChecked
                )
                val config = Config(range, jurisdictions)
                db.collection("lawyers").document(user.uid).update("config", config)
                changeFragment()
            }
        }.addOnFailureListener{
            Log.i("LAWYERS_RETRIEVE_ERROR", "Erro: $it")
        }
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()){
                val range:Double = (mapsRange.progress / 10).toDouble()
                val jurisdictions = listOf(
                    jurisdiction1.isChecked,
                    jurisdiction2.isChecked,
                    jurisdiction3.isChecked,
                    jurisdiction4.isChecked,
                    jurisdiction5.isChecked,
                    jurisdiction6.isChecked
                )
                val config = Config(range, jurisdictions)
                db.collection("offices").document(user.uid).update("config", config)
                changeFragment()
            }
        }.addOnFailureListener{
            Log.i("OFFICES_RETRIEVE_ERROR", "Erro: $it")
        }
    }

    fun changeFragment() {
        val transaction = fragmentManager?.beginTransaction()
        val fragment = HomeFragment()
        transaction?.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
        transaction?.replace(R.id.nav_host_fragment, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

}