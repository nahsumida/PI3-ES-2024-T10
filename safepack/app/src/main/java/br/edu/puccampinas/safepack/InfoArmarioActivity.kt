package br.edu.puccampinas.safepack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmarioBinding

class InfoArmarioActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityInfoArmarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alugarArmarioButton.setOnClickListener {
            val iAlugarArmario = Intent(this, AlugarArmarioActivity::class.java)
            startActivity(iAlugarArmario)
        }

        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }

        binding.irAoLocalButton.setOnClickListener {
            val latitude = -22.8360456
            val longitude = -47.0564085
            val uri = "geo:$latitude,$longitude"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

            intent.`package` = "com.google.android.apps.maps"

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google maps não está instalado",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}