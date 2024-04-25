package br.edu.puccampinas.safepack.model

class Pessoa (var nomeCompleto: String,
              var cpf: String,
              var dataNascimento: String,//DateTime,
              var telefone:String,
              var senha: String,
              var authID: String,
              var ehGerente: Boolean) {

    public fun isNomeValido(): Boolean {
        val regex = Regex("^[^0-9@#$%^&+=]*\$")
        return regex.matches(nomeCompleto)
    }

    public fun isDataNascimentoValida(): Boolean {
        val regex = Regex("^\\d{2}/\\d{2}/\\d{4}\$")
        return regex.matches(dataNascimento)
    }

    public fun isCpfValido(): Boolean {
        val regex = Regex("^[0-9]{11}\$")
        return regex.matches(cpf)
    }

    public fun isTelefoneValido(): Boolean {
        val regex = Regex("^[0-9]{11}\$")
        return regex.matches(telefone)
    }

    public fun isSenhaValida(): Boolean {
        return senha.length >= 6
    }
}