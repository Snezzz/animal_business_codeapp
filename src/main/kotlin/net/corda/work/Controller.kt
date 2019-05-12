package net.corda.work

import Flow.Flow
import net.corda.core.crypto.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import state.BusinessState
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

val SERVICE_NODE_NAMES = listOf("Notary", "NetworkMapService")


@Path("data")
class workCertificateApi(val services: CordaRPCOps) {
    //find legal name of current Node
    private val myLegalName: String = services.nodeIdentity().legalIdentity.name


    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    fun me() = mapOf("me" to myLegalName)

    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    fun peers() = mapOf("peers" to services.networkMapUpdates().first
            .map { it.legalIdentity.name }
            .filter { it != myLegalName && it !in SERVICE_NODE_NAMES })

    //create a query(an order) from client with parameters 'buyer' and 'animal'
    @GET
    @Path("new")
    @Produces(MediaType.APPLICATION_JSON)
    fun newOrder(@QueryParam(value = "buyer") buyer: String, @QueryParam(value = "animal") animal: String): Response {

        //get node of buyer if it is exist
        val orderRecordTo = services.partyFromName(buyer) ?:
            throw IllegalArgumentException("Unknown party name")

        //start the Flow working with current client and his query of new animal
        services.startFlowDynamic(Flow::class.java, orderRecordTo, animal).returnValue.get()
        return Response.status(Response.Status.CREATED).
                entity("Everything is OK! You just write down an order " +
                        "to ${orderRecordTo.name}").build()
    }

    //to get all orders
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun orderRecords() = services.vaultAndUpdates().first.filter { it.state.data is BusinessState }

}



class Plugin : CordaPluginRegistry() {
    override val webApis = listOf(Function(::workCertificateApi))
    override val requiredFlows = mapOf(Flow::class.java.name to
            setOf(Party::class.java.name, String::class.java.name))
    override val servicePlugins: List<Function<PluginServiceHub, out Any>> = listOf()
    override val staticServeDirs = mapOf("work" to javaClass.classLoader.getResource("web").toExternalForm())
}