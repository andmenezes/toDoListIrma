package com.example.todolist

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow



class NoteViewModel(private val repository: Repository) : ViewModel() {

    /**
     * Esse aqui é o paranaue da coisa esse LiveData é exatamente o que faz a ligação do BIND
     *  e fica escutando o OBSERVER da main Active, importante importar esse trequinho ai! e na
     *  mainActive usar o metodo observer pra ficar vendo quando esse cara ai atualizar,
     *  ainda tentando descobrir quando vou dar fetch na tabela! Descobri na real não faço o fetch
     *  eu fico observando quando a tabela muda ai ele faz o fetch sozinho isso é uma magica que não entendi mas funciona
     */

    /** Mais um bloco aqui pq isso merece!
     * Converter um tipo de dado de FLOW para LiveData é necessário adicionar o "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0" no graddle
     * foram 48 minutos 2 copos de wiskys e 18 fios de cabelos arrancados em momento de total desespero para resolver isso!!!
     *
     */


    val allNotes: LiveData<List<Notes>> = repository.allNotes.asLiveData()

    /**
     * Criando o viewModel já recebendo um repository INSTANCIADO!!!! Internet diverge sobre isso mas ta funcionando!
     * PS: NUNCA CRIAR UM REPOSITORIO CHAMADO REPOSITORIO NESSA BULLDEGA!!!!!!
     */
        fun addData(notes: Notes){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addData(notes)
        }
    }
}

/**
 * Copiado da internet!!! MUAHAHAHAHSHAS
 */
class NoteViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
