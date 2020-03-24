package br.project_advhevogoober_final

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.OfficeProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile_offer_details.*


class ProfileOfferDetailsActivity : AppCompatActivity() {
    private val db= FirebaseFirestore.getInstance()
    private val user= FirebaseAuth.getInstance().currentUser!!
    var storageReference= FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_offer_details)
        val id = intent.getStringExtra("id")


        db.collection("lawyers").document(id).get().addOnSuccessListener {
            if (it.exists()) {
                var lawyerProfile = it.toObject(LawyerProfile::class.java)

                txtVwDNomeOffer.text = lawyerProfile!!.name
                txtVwDSobrenomeOffer.text = lawyerProfile!!.surname
                txtVwDTelefoneOffer.text = lawyerProfile!!.phone
                txtVwDEmailOffer.text = user.email
                txtVwDOABOffer.text = lawyerProfile!!.oab_code

            }

        }
        db.collection("offices").document(id).get().addOnSuccessListener {
            if (it.exists()) {
                var officeProfile = it.toObject(OfficeProfile::class.java)
                txtVwDNomeOffer.text = officeProfile!!.name
                txtVwDTelefoneOffer.text = officeProfile!!.phone
                txtVwDEmailOffer.text = user.email
                textView6.isVisible=false
                txtVwDSobrenomeOffer.isVisible=false
                textView11.isVisible = false
                txtVwDOABOffer.isVisible = false


            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
        }

        var tarefa=storageReference.child("profileImages/"+user.uid).getBytes(1024*1024)
        tarefa.addOnSuccessListener {
            if (it!=null){
                progressBarOffer.visibility= View.GONE
                for (x in 0 until layoutProfileOffer.childCount ){
                    var daodao: View =layoutProfileOffer.getChildAt(x)
                    if (daodao !is ProgressBar){
                        daodao.visibility=View.VISIBLE
                    }
                }
                Toast.makeText(this,"Completou com sucesso",Toast.LENGTH_LONG).show()
                var imagem= BitmapFactory.decodeByteArray(it,0,it.size)
                imgVwPhotoOfferPrefile.setImageBitmap(imagem)
            }
            else{
                Toast.makeText(this,"Erro ao carregar a imagem de perfil",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
            progressBarOffer.visibility= View.GONE
        }

    }
}
