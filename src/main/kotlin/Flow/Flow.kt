package Flow

import co.paralleluniverse.fibers.Suspendable
import contracts.BusinessContract
import net.corda.core.contracts.Command
import net.corda.core.contracts.TransactionType
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.flows.FinalityFlow
import state.BusinessState

// Flow
public class Flow(val buyer: Party, val animal: String): FlowLogic<SignedTransaction>() {
    override val progressTracker: ProgressTracker = Flow.Companion.tracker()

    //define basic stages
    companion object {
        object CREATING_STAGE : ProgressTracker.Step("Creating a new order...")
        object VERIFYING_STAGE : ProgressTracker.Step("Verifying current order...")
        object SENDING_STAGE : ProgressTracker.Step("Write down a new order...")
        fun tracker() = ProgressTracker(Flow.Companion.CREATING_STAGE,
                Flow.Companion.VERIFYING_STAGE, Flow.Companion.SENDING_STAGE)
    }

    @Suspendable
    override fun call(): SignedTransaction {
        //define current store
        val me = serviceHub.myInfo.legalIdentity
        //define notary
        val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity
        //set steps
        progressTracker.currentStep = Flow.Companion.CREATING_STAGE
        //creating state: send new data with store
        val signature = TransactionType.General.Builder(notary)
                .withItems(BusinessState(me, buyer,  "12 May 2019", animal),
                        Command(BusinessContract.Send(), listOf(me.owningKey)))
                .signWith(serviceHub.legalIdentityKey)
                .toSignedTransaction(true)
        //verifying signature
        progressTracker.currentStep = Flow.Companion.VERIFYING_STAGE
        signature.tx.toLedgerTransaction(serviceHub).verify()
        //if everything is OK, request signature by notary service and broadcast transaction to participants
        progressTracker.currentStep = Flow.Companion.SENDING_STAGE
        return subFlow(FinalityFlow(signature, setOf(me, buyer))).single()
    }
}