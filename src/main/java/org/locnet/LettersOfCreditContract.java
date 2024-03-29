/*
 * SPDX-License-Identifier: 
 */
package org.locnet;

import org.awjh.Organization;
import org.awjh.Participant;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.locnet.assets.Approval;
import org.locnet.assets.Evidence;
import org.locnet.assets.LetterOfCredit;
import org.locnet.assets.ProductDetails;
import org.locnet.enums.Status;
import org.locnet.utils.LetterOfCreditContext;

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Contract(name = "LettersOfCreditContract", 
    info = @Info(title = "Letters of Credit contract", 
                description = "", 
                version = "0.0.1", 
                license = 
                        @License(name = "SPDX-License-Identifier: ", 
                                url = ""), 
                                contact =  @Contact(email = "java-contract@example.com", 
                                                name = "java-contract", 
                                                url = "http://java-contract.me")))
@Default
public class LettersOfCreditContract extends BaseContract {
    public  LettersOfCreditContract() {}

    @Transaction()
    public String helloWorld(LetterOfCreditContext ctx) {
        final Participant participant = ctx.getClientIdentity().getParticipant();
        final Organization organization = ctx.getClientIdentity().getOrganization();

        return "HELLO " + participant.getId() + " FROM " + organization.getName();
    }

    @Transaction()
    public void apply(LetterOfCreditContext ctx, String letterId, String applicantId, String beneficiaryId, String issuingBankId, String exportingBankId, String[] rules, ProductDetails productDetails) {
        final Approval approval = new Approval(true, false, false, false);

        final LetterOfCredit loc = new LetterOfCredit(letterId, applicantId, beneficiaryId, issuingBankId, exportingBankId, rules, productDetails, new Evidence[0], approval, Status.AWAITING_APPROVAL);

        ctx.getLetterOfCreditList().add(loc);
    }

    @Transaction()
    public LetterOfCredit read(LetterOfCreditContext ctx, String letterId) {
        return (LetterOfCredit) ctx.getLetterOfCreditList().get(letterId);
    }
}