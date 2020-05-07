package com.iplticket.flows

import co.paralleluniverse.fibers.Suspendable
import com.iplticket.states.IplTicketState
import com.r3.corda.lib.tokens.contracts.utilities.withNotary
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens
import net.corda.core.contracts.TransactionState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
@StartableByRPC
class CreateIplTicketTokenFlow(private val ticketTeam: String) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call():String {

        //get the notary
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        //create token type by passing in the name of the ipl match. specify the maintainer as BCCI
        val id = UniqueIdentifier()
        val iplTicket = IplTicketState(id, ticketTeam, ourIdentity)

        //warp it with transaction state specifying the notary
        val transactionState = iplTicket withNotary notary

        //call built in sub flow CreateEvolvableTokens to craete the base type on BCCI node
        val stx = subFlow(CreateEvolvableTokens(transactionState))

        return "Ticket for $ticketTeam has been created. With Ticket ID: $id" +
                "\ntxId: ${stx.id}"
    }
}
