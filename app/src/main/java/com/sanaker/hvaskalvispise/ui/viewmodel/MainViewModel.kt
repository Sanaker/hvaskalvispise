package com.sanaker.hvaskalvispise.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sanaker.hvaskalvispise.data.model.Dish
import com.sanaker.hvaskalvispise.data.model.DishDao
import com.sanaker.hvaskalvispise.util.SingleLiveEvent // Forutsetter at denne er korrekt implementert
import kotlinx.coroutines.launch

// Resultat-wrapper for å legge til matrett
sealed class AddDishResult {
    data class Success(val dish: Dish) : AddDishResult() // Sørg for at Success tar et Dish objekt
    object Duplicate : AddDishResult()
    object EmptyName : AddDishResult()
}

class MainViewModel(private val dishDao: DishDao) : ViewModel() {

    // --- LiveData for alle retter fra databasen ---
    // Brukes internt og potensielt av DishListActivity (som uiVisibleDishes nedenfor)
    val allDishes: LiveData<List<Dish>> = dishDao.getAllDishes().asLiveData()

    // --- LiveData for MainActivity sin "tilfeldig valgt rett" detaljvisning ---
    private val _randomlySelectedDish = MutableLiveData<Dish?>()
    val randomlySelectedDish: LiveData<Dish?> get() = _randomlySelectedDish

    // --- LiveData for resultat av å legge til rett (felles) ---
    private val _addDishResult = SingleLiveEvent<AddDishResult?>()
    val addDishResult: LiveData<AddDishResult?> = _addDishResult

    // --- LiveData og properties for den "gamle" / alternative velg-tilfeldig-logikken (med historikk) ---
    private val _selectedDish = MutableLiveData<Dish?>() // Kanskje brukt internt eller av en annen UI-del
    val selectedDish: LiveData<Dish?> = _selectedDish

    private val recentlyChosenDishes = mutableListOf<Dish>()
    companion object {
        private const val RECENT_CHOICES_HISTORY_SIZE = 3 // Antall nylige valg å huske for chooseRandomDish
    }

    // --- LiveData og properties for DishListActivity (med "Angre sletting") ---
    private val _uiVisibleDishes = MediatorLiveData<List<Dish>>()
    val uiVisibleDishes: LiveData<List<Dish>> get() = _uiVisibleDishes

    private val _showUndoSnackbar = SingleLiveEvent<Dish>() // For "Angre sletting"
    val showUndoSnackbar: LiveData<Dish> get() = _showUndoSnackbar

    private var recentlyDeletedDishInternal: Dish? = null // For "Angre sletting"

    init {
        // Koble uiVisibleDishes til allDishes for DishListActivity
        _uiVisibleDishes.addSource(allDishes) { originalDishList ->
            if (recentlyDeletedDishInternal == null) {
                _uiVisibleDishes.value = originalDishList
            } else {
                // Filtrer ut den "nylig slettede" retten fra listen UI ser
                _uiVisibleDishes.value = originalDishList?.filter { it.id != recentlyDeletedDishInternal!!.id } ?: emptyList()
            }

            // Håndter tilfeller der _selectedDish (den med historikk) fjernes fra listen
            val currentSelectedForHistory = _selectedDish.value
            if (currentSelectedForHistory != null && originalDishList?.contains(currentSelectedForHistory) == false) {
                _selectedDish.value = null
                recentlyChosenDishes.remove(currentSelectedForHistory)
            }

            // Håndter tilfeller der _randomlySelectedDish (for MainActivity) fjernes fra listen
            val currentRandomlySelected = _randomlySelectedDish.value
            if (currentRandomlySelected != null && originalDishList?.contains(currentRandomlySelected) == false){
                _randomlySelectedDish.value = null
            }
        }
    }

    // --- Metoder for MainActivity ---

    /**
     * Velger en tilfeldig rett fra listen og oppdaterer _randomlySelectedDish.
     * Dette er den enkle versjonen uten historikk, for MainActivitys hovedvisning.
     */
    fun getRandomDish() {
        viewModelScope.launch {
            val currentDishes = allDishes.value
            if (!currentDishes.isNullOrEmpty()) {
                _randomlySelectedDish.postValue(currentDishes.random())
            } else {
                _randomlySelectedDish.postValue(null)
            }
        }
    }

    /**
     * Legger til en ny matrett i databasen.
     * Forventer et Dish-objekt (MainActivity sender et med kun navn).
     */
    fun addDish(dish: Dish) {
        viewModelScope.launch {
            if (dish.name.isBlank()) {
                _addDishResult.value = AddDishResult.EmptyName
                return@launch
            }
            val existingDish = dishDao.getDishByName(dish.name) // Forutsetter at DAO har getDishByName
            if (existingDish != null) {
                _addDishResult.value = AddDishResult.Duplicate
            } else {
                dishDao.insertDish(dish)
                // Det er lurt å hente retten fra databasen igjen for å få ID-en hvis den er autogenerert
                // og for å sikre at det er det faktiske lagrede objektet.
                // For enkelhets skyld sender vi tilbake det innsendte 'dish'-objektet.
                // En mer robust løsning ville vært å returnere det faktiske objektet fra DAO etter insert.
                _addDishResult.value = AddDishResult.Success(dish)
            }
        }
    }

    /**
     * Kalles etter at resultatet fra addDish er håndtert i UI.
     */
    fun onAddDishResultHandled() {
        _addDishResult.value = null
    }

    // --- Metoder for den "gamle"/alternative tilfeldig-valg-logikken (med historikk) ---

    /**
     * Velger en tilfeldig rett med hensyn til nylig valgte retter (oppdaterer _selectedDish).
     */
    fun chooseRandomDish() {
        viewModelScope.launch {
            val allDishesList = allDishes.value ?: return@launch

            if (allDishesList.isEmpty()) {
                _selectedDish.postValue(null)
                return@launch
            }

            if (allDishesList.size == 1) {
                val singleDish = allDishesList.first()
                _selectedDish.postValue(singleDish)
                updateRecentlyChosen(singleDish)
                return@launch
            }

            val candidates = allDishesList.filter { it !in recentlyChosenDishes }
            val chosenDish: Dish?

            chosenDish = if (candidates.isNotEmpty()) {
                candidates.random()
            } else {
                // Hvis alle har vært valgt nylig, velg en helt tilfeldig og nullstill historikken for den
                recentlyChosenDishes.clear() // Vurder om dette er ønsket oppførsel
                allDishesList.random()
            }

            _selectedDish.postValue(chosenDish)
            if (chosenDish != null) {
                updateRecentlyChosen(chosenDish)
            }
        }
    }

    private fun updateRecentlyChosen(dish: Dish) {
        recentlyChosenDishes.remove(dish) // Fjern hvis den finnes for å legge den bakerst
        recentlyChosenDishes.add(dish)
        while (recentlyChosenDishes.size > RECENT_CHOICES_HISTORY_SIZE) {
            recentlyChosenDishes.removeAt(0)
        }
    }

    // --- Generelle CRUD-operasjoner og for DishListActivity ---

    fun updateDish(dish: Dish) {
        viewModelScope.launch {
            dishDao.updateDish(dish)
            // allDishes vil oppdateres automatisk via Flow.
            // Oppdater LiveData for spesifikt valgte retter hvis de er den redigerte.
            if (_selectedDish.value?.id == dish.id) {
                _selectedDish.value = dish
            }
            if (_randomlySelectedDish.value?.id == dish.id) {
                _randomlySelectedDish.value = dish
            }
            // Oppdater i historikklisten også
            val indexInHistory = recentlyChosenDishes.indexOfFirst { it.id == dish.id }
            if (indexInHistory != -1) {
                recentlyChosenDishes[indexInHistory] = dish
            }
        }
    }

    /**
     * Sletter alle matretter fra databasen.
     */
    fun clearAllDishes() {
        viewModelScope.launch {
            dishDao.deleteAllDishes()
            // allDishes vil oppdateres automatisk.
            // Nullstill valgte retter og historikk.
            _selectedDish.value = null
            _randomlySelectedDish.value = null
            recentlyChosenDishes.clear()
            recentlyDeletedDishInternal = null // Sikre at angre-sletting tilstand er ren
        }
    }

    // --- Metoder spesifikt for "Angre sletting" i DishListActivity ---

    fun onDishDeleteRequested(dish: Dish, position: Int) { // position er kanskje ikke nødvendig hvis ListAdapter brukes smart
        recentlyDeletedDishInternal = dish
        // UI vil reflektere endringen via _uiVisibleDishes's observasjon av allDishes og filtrering
        // Trenger kanskje en .call() eller .setValue() for å tvinge re-evaluering hvis filtreringen ikke er umiddelbar.
        // For nå stoler vi på at MediatorLiveData reagerer når recentlyDeletedDishInternal settes.
        _uiVisibleDishes.removeSource(allDishes) // Re-add for å tvinge refresh med ny filtertilstand
        _uiVisibleDishes.addSource(allDishes) { originalDishList ->
            if (recentlyDeletedDishInternal == null) {
                _uiVisibleDishes.value = originalDishList
            } else {
                _uiVisibleDishes.value = originalDishList?.filter { it.id != recentlyDeletedDishInternal!!.id } ?: emptyList()
            }
        }
        _showUndoSnackbar.value = dish // Signaliser at Snackbar skal vises
    }

    fun confirmDeleteDish() {
        recentlyDeletedDishInternal?.let { dishToDelete ->
            viewModelScope.launch {
                dishDao.deleteDish(dishToDelete)
                // allDishes (og dermed _uiVisibleDishes) vil oppdateres.
            }
        }
        recentlyDeletedDishInternal = null
        // Tving en oppdatering av uiVisibleDishes for å fjerne filteret
        _uiVisibleDishes.removeSource(allDishes)
        _uiVisibleDishes.addSource(allDishes) { originalDishList ->
            _uiVisibleDishes.value = originalDishList // Nå uten filter
        }
    }

    fun undoDeleteDish() {
        recentlyDeletedDishInternal = null
        // Tving en oppdatering av uiVisibleDishes for å fjerne filteret
        _uiVisibleDishes.removeSource(allDishes)
        _uiVisibleDishes.addSource(allDishes) { originalDishList ->
            _uiVisibleDishes.value = originalDishList // Nå uten filter
        }
    }
}
