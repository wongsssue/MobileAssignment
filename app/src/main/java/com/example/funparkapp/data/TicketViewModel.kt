import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funparkapp.R
import com.example.funparkapp.data.Ticket
import com.example.funparkapp.data.TicketRepository
import com.example.funparkapp.data.TicketType
import com.example.funparkapp.data.TicketWithTicketType
import kotlinx.coroutines.launch

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    val allTickets: LiveData<List<Ticket>> = repository.getAllTickets()

    fun getTicketWithTicketType(ticketPlan: String): LiveData<List<TicketWithTicketType>> {
        Log.d("TicketViewModel", "Requesting ticket with types for: $ticketPlan")
        return repository.getTicketWithTicketType(ticketPlan)
    }

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    private suspend fun initializeData() {
        val tickets = repository.getAllTickets().value
        if (tickets.isNullOrEmpty()) {
            Log.d("TicketViewModel", "Initializing data...")

            val ticketsWithTypes = listOf(
                Pair(
                    Ticket(ticketPlan = "One Day Pass", ticketPlanDescription = "Experience unlimited access to all attractions for a single day, perfect for a thrilling adventure packed into one visit.", imageResId = R.drawable.onedaypass),
                    listOf(
                        TicketType(ticketType = "Adult", price = 160.00, ticketDescription = "(Age 13 years old and above)", ticketPlan = ""),
                        TicketType(ticketType = "Child/Senior Citizen", price = 135.00, ticketDescription = "(Age 12 years old and below for child and age 60 years old and above for senior)", ticketPlan = "")
                    )
                ),
                Pair(
                    Ticket(ticketPlan = "Two Day Pass", ticketPlanDescription = "Enjoy the flexibility of exploring attractions at a relaxed pace over two consecutive days, ideal for making the most of your experience.", imageResId = R.drawable.twodaypass),
                    listOf(
                        TicketType(ticketType = "Adult", price = 260.00, ticketDescription = "(Age 13 years old and above)", ticketPlan = "" ),
                        TicketType(ticketType = "Child/Senior Citizen", price = 204.00, ticketDescription = "(Age 12 years old and below for child and age 60 years old and above for senior)", ticketPlan = "")
                    )
                ),
                Pair(
                    Ticket(ticketPlan = "Ultimate Pass (1 Year)", ticketPlanDescription = "Get unlimited access for an entire year, along with exclusive perks and discounts, perfect for dedicated fans and frequent visitors.", imageResId = R.drawable.ultimatepass),
                    listOf(
                        TicketType(ticketType = "Adult", price = 548.00, ticketDescription = "(Age 13 years old and above)", ticketPlan = ""),
                        TicketType(ticketType = "Child/Senior Citizen", price = 398.00, ticketDescription = "(Age 12 years old and below for child and age 60 years old and above for senior)", ticketPlan = "")
                    )
                )
            )

            ticketsWithTypes.forEach { (ticket, ticketTypes) ->
                repository.insertTicket(ticket)
                viewModelScope.launch {
                    ticketTypes.forEach { type ->
                        val ticketTypeEntity = type.copy(ticketPlan = ticket.ticketPlan)
                        repository.insertTicketType(ticketTypeEntity)
                    }
                }
            }

        }
    }

    fun insert(ticket: Ticket, ticketTypes: Map<String, Pair<Double, String>>) = viewModelScope.launch {
        repository.insertTicket(ticket)
        ticketTypes.forEach { (type, value) ->
            val (price, description) = value
            val ticketTypeEntity = TicketType(
                ticketType = type,
                price = price,
                ticketDescription = description,
                ticketPlan = ticket.ticketPlan
            )
            repository.insertTicketType(ticketTypeEntity)
        }
    }


    fun delete(ticket: Ticket) = viewModelScope.launch {
        repository.deleteTicket(ticket.ticketPlan)
    }

}