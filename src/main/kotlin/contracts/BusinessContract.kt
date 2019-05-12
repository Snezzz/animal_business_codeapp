package contracts

import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash
import state.BusinessState


// Contract and state.
public class BusinessContract : Contract {
    class Send : TypeOnlyCommandData()
    override val legalContractReference: SecureHash = SecureHash.sha256("Yo!")

    //validation check
    override fun verify(tx: TransactionForContract) = requireThat {
        val command = tx.commands.requireSingleCommand<Send>()
        "There can be no inputs when you want to find animal." by (tx.inputs.isEmpty())
        "There must be one output" by (tx.outputs.size == 1)
        val newAnimal = tx.outputs.single() as BusinessState
        "You can't to buy animal from yourself!" by ( newAnimal.buyer != newAnimal.store)
        //order must be confirmed by seller
        "It must be signed by the store." by (newAnimal.store.owningKey == command.signers.single())
        //it can do only seller
        "You are not a store!" by (Regex("Store").
                containsMatchIn(newAnimal.store.name))
    }

}
