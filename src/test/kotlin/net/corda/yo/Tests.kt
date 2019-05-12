package net.corda.work

import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test


class VaccinationTests {

    lateinit var net: MockNetwork
    lateinit var a: MockNetwork.MockNode
    lateinit var b: MockNetwork.MockNode

    @Before
    fun setup() {
        net = MockNetwork()
        val nodes = net.createSomeNodes(2)
        a = nodes.partyNodes[0]
        b = nodes.partyNodes[1]
        net.runNetwork()
    }

    @After
    fun tearDown() {
        net.stopNodes()
    }

    @Test
    fun messageTransactionMustBeWellFormed() {
        /*// A pre-made Yo to Bob.
        val yo = VaccinationRecord.BusinessState(ALICE, BOB, Calendar.getInstance().getTime().toString(), "Yo!")
        // A pre-made dummy state.
        val dummyState = object : ContractState {
            override val contracts get() = DUMMY_PROGRAM_ID
            override val participants: List<CompositeKey> get() = listOf()
        }
        // A pre-made dummy command.
        class DummyCommand : TypeOnlyCommandData()
        // Tests.
        ledger {
            // input state present.
            transaction {
                input { dummyState }
                command(ALICE_PUBKEY) { VaccinationRecord.Send() }
                output { yo }
                this.failsWith("There can be no inputs when work record patient.")
            }
            // No command.
            transaction {
                output { yo }
                this.failsWith("")
            }
            // Wrong command.
            transaction {
                output { yo }
                command(ALICE_PUBKEY) { DummyCommand() }
                this.failsWith("")
            }
            // Command signed by wrong key.
            transaction {
                output { yo }
                command(MINI_CORP_PUBKEY) { VaccinationRecord.Send() }
                this.failsWith("The work record must be signed by the doctor.")
            }
            // Sending to yourself is not allowed.
            transaction {
                output { VaccinationRecord.BusinessState(ALICE, ALICE, Calendar.getInstance().getTime().toString(), "Yo!") }
                command(ALICE_PUBKEY) { VaccinationRecord.Send() }
                this.failsWith("U can't make work to yourself!")
            }
            transaction {
                output { yo }
                command(ALICE_PUBKEY) { VaccinationRecord.Send() }
                this.verifies()
            }
        }*/
    }

    @Test
    fun flowWorksCorrectly() {
        /*val yo = VaccinationRecord.BusinessState(a.info.legalIdentity, b.info.legalIdentity, Calendar.getInstance().getTime().toString(), "Yo!")
        val flow = vaccinationRecordFlow(b.info.legalIdentity, "Yo!")
        val future = a.services.startFlow(flow).resultFuture
        net.runNetwork()
        val stx = future.getOrThrow()
        // Check yo transaction is stored in the storage service and the state in the vault.
        databaseTransaction(b.database) {
            val bTx = b.storage.validatedTransactions.getTransaction(stx.id)
            assertEquals(bTx, stx)
            print("$bTx == $stx")
            val bYo = b.vault.unconsumedStates<VaccinationRecord.BusinessState>().single().state.data
            // Strings match but the linearId's will differ.
            assertEquals(bYo.toString(), yo.toString())
            print("$bYo == $yo")
        }*/
    }
}
