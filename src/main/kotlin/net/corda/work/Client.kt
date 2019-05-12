package net.corda.work

import com.google.common.net.HostAndPort
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import rx.Observable
import state.BusinessState

fun main(args: Array<String>) {
    ChatRPC().main(args)
}

private class ChatRPC {
    companion object {
        val current_logger: Logger = loggerFor<ChatRPC>()
    }
    fun main(args: Array<String>) {
        require(args.size == 1) { "Usage: TemplateClientRPC <node address:port>" }
        val nodeAddress = HostAndPort.fromString(args[0])
        val client = CordaRPCClient(nodeAddress)
        client.start("user1", "test")
        val proxy = client.proxy()
        val (transactions: List<SignedTransaction>, futureTransactions: Observable<SignedTransaction>) =
                proxy.verifiedTransactions()
        futureTransactions.startWith(transactions).toBlocking().subscribe { transaction ->
            transaction.tx.outputs.forEach { output ->
                val state = output.data as BusinessState
                current_logger.info(state.toString())
            }
        }
    }
}
