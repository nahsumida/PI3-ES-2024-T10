import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

class FunctionsRepository {

    private val functions = FirebaseFunctions.getInstance("southamerica-east1")

    fun setFimLocacao(
        idLocacao: String,
        valEstorno: Double
    ) {
        val data = hashMapOf(
            "idLocacao" to idLocacao,
            "valEstorno" to valEstorno
        )
        functions
            .getHttpsCallable("addEstornoLocacao")
            .call(data)
            .continueWith { task ->
                if(task.isSuccessful) {
                    Log.e("SUCCESS", "Sucesso ao chamar funcao")
                }
            }
            .addOnFailureListener{
                Log.e("ERROR", "Erro ao chamar funcao: $it")
            }
    }
}