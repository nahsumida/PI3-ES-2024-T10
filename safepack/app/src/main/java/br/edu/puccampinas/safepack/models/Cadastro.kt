package br.edu.puccampinas.safepack.models

class Cadastro (var nomeCompleto: String,
                var cpf: String,
                var dataNascimento: String,
                var telefone:String,
                var senha: String) {

    public fun isNomeValido(): Boolean {
        val regex = Regex("^[^0-9@#$%^&+=]*\$")
        return regex.matches(nomeCompleto)
    }

    public fun isDataNascimentoValida(): Boolean {
        return true
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