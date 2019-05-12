package state

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.CompositeKey
import net.corda.core.crypto.Party
import net.corda.core.crypto.keys


import java.security.PublicKey


data class BusinessState(val store: Party,
                         val buyer: Party,
                         val date: String,
                         val animal: String,
                         override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState {
  //define the contract
    override val contract get() = contracts.BusinessContract()
   //The list of entities considered to have a stake in this state.
    override val participants: List<CompositeKey> get() = listOf(store.owningKey, buyer.owningKey)
    override fun isRelevant(ourKeys: Set<PublicKey>) = ourKeys.intersect(participants.keys).isNotEmpty()
    override fun toString() = "${store.name} -> ${buyer.name}: $animal"
}