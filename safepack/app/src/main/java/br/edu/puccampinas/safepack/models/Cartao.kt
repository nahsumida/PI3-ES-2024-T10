package br.edu.puccampinas.safepack.models

data class Cartao (var nomeTitular: String,
                   var dataValidade: String,
                   var CVV: String,
                   var numCartao:String) {
    public fun isNomeValido(): Boolean {
        val regex = Regex("^[^0-9@#$%^&+=]*\$")
        return regex.matches(nomeTitular)
    }

    public fun isDataValidadeValida(): Boolean {
        val regex = Regex("^\\d{2}/\\d{2}\$")
        return regex.matches(dataValidade)
    }
    public fun isCVVValido(): Boolean {
        val regex = Regex("^[0-9]{3}\$")
        return regex.matches(CVV)
    }

    public fun isNumCartaoValido(): Boolean {
        val regex = Regex("^[0-9]{16}\$")
        return regex.matches(numCartao)
    }
}
