package net.corda.work

import com.google.common.util.concurrent.Futures
import net.corda.core.getOrThrow
import net.corda.core.node.services.ServiceInfo
import net.corda.node.driver.driver
import net.corda.node.services.transactions.ValidatingNotaryService
import net.corda.nodeapi.User

fun main(args: Array<String>) {

    val user = User("user1", "test", permissions = setOf())

    driver(isDebug = true) {
       //start nodes
        startNode("Notary", setOf(ServiceInfo(ValidatingNotaryService.Companion.type)))
        val (RussianAnimalStore, EnglishAnimalStore, BuyerVasya, BuyerPetya) = Futures.allAsList(
                //providedName = legal name in Party, rpcUsers = who can use RPC system
                startNode("RussianAnimalStore", rpcUsers = listOf(user)),
                startNode("EnglishAnimalStore", rpcUsers = listOf(user)),
                startNode("BuyerVasya", rpcUsers = listOf(user)),
                startNode("BuyerPetya", rpcUsers = listOf(user))).getOrThrow()

        //start web-server for every node
        startWebserver(RussianAnimalStore)
        startWebserver(EnglishAnimalStore)
        startWebserver(BuyerVasya)
        startWebserver(BuyerPetya)
        waitForAllNodesToFinish()
    }
}
