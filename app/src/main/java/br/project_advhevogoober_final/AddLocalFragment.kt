package br.project_advhevogoober_final

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.API.RetrofitBuilder
import br.project_advhevogoober_final.Model.APIResultsObject
import br.project_advhevogoober_final.Service.DAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_add_local.*
import kotlinx.android.synthetic.main.fragment_add_local.view.*
import org.imperiumlabs.geofirestore.GeoFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocalFragment:Fragment() {

    val db = FirebaseFirestore.getInstance()
    val collectionReference = db.collection("Offers")
    val geoFirestore = GeoFirestore(collectionReference)
    val retrofit = RetrofitBuilder.getInstance()
    val service = retrofit?.create(DAO::class.java)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"
    val manager = fragmentManager

    val TAG ="TesteFragment"

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
        val view: View =inflater!!.inflate(R.layout.fragment_add_local,container,false)
        view.local_add.setOnClickListener {
            if (
                !view.local_street.text.isNullOrEmpty() &&
                !view.local_city.text.isNullOrEmpty() &&
                !view.local_state.text.isNullOrEmpty() &&
                !view.local_postal_code.text.isNullOrEmpty()
            ) {
                service?.show(
                    key,
                    view.local_street.text.toString(),
                    view.local_city.text.toString(),
                    view.local_state.text.toString(),
                    view.local_postal_code.text.toString()
                )?.enqueue(object : Callback<APIResultsObject> {
                    override fun onFailure(call: Call<APIResultsObject>, t: Throwable) {
                        Toast.makeText(activity, "Ocorreu um erro na inserção do local.", Toast.LENGTH_LONG).show()
                        Log.i("Erro de chamada da API: ", t.toString())
                    }

                    override fun onResponse(
                        call: Call<APIResultsObject>,
                        response: Response<APIResultsObject>
                    ) {
                        val lat : Double = response?.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lat!!
                        val long : Double = response?.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lng!!

                        db.collection("lawyers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                geoFirestore.setLocation(it.id, GeoPoint(lat, long))
                                val transaction = manager?.beginTransaction()
                                val fragment = HomeFragment()
                                transaction?.replace(R.id.nav_host_fragment, fragment)
                                transaction?.addToBackStack(null)
                                transaction?.commit()
                            }
                            else {
                                Toast.makeText(activity, "Documento não encontrado nos advogados!", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener{
                            Toast.makeText(activity,"Erro ao adicionar local",Toast.LENGTH_LONG).show()
                            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
                        }
                        db.collection("offices").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                            if (it.exists()){
                                geoFirestore.setLocation(it.id, GeoPoint(lat, long))
                                val transaction = manager!!.beginTransaction()
                                val fragment = HomeFragment()
                                transaction.replace(R.id.nav_host_fragment, fragment)
                                transaction.addToBackStack(null)
                                transaction.commit()
                            }
                            else {
                                Toast.makeText(activity, "Documento não encontrado nos escritórios!", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener{
                            Toast.makeText(activity,"Erro ao adicionar local",Toast.LENGTH_LONG).show()
                            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
                        }
                    }
                })
            } else {
                Toast.makeText(activity, "Preencha todos os campos antes de continuar!", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }


}