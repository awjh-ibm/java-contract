package org.locnet.utils;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;

import com.google.protobuf.InvalidProtocolBufferException;

import org.awjh.ledger_api.lists.AssetList;
import org.awjh.ledger_api.lists.OrganizationList;
import org.awjh.ledger_api.lists.ParticipantList;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.locnet.assets.LetterOfCredit;
import org.locnet.organizations.Bank;
import org.locnet.participants.Task;

public class LetterOfCreditContext extends Context {

    private OrganizationList organizationList;
    private ParticipantList participantList;
    private AssetList letterOfCreditList;
    private LetterOfCreditClientIdentity clientIdentity;

    @SuppressWarnings("unchecked")
    public LetterOfCreditContext(ChaincodeStub stub)
            throws InvalidProtocolBufferException, CertificateException, UnsupportedEncodingException {
        super(stub);

        this.organizationList = new OrganizationList(this, "locnet.organizations", new Class[] {Bank.class});
        this.participantList = new ParticipantList(this, "locnet.participants", new Class[] {Task.class});
        this.letterOfCreditList = new AssetList(this, "locnet.assets", new Class[] {LetterOfCredit.class});

        this.clientIdentity = new LetterOfCreditClientIdentity(this);
    }

    public OrganizationList getOrganizationList() {
        return this.organizationList;
    }

    public ParticipantList getParticipantList() {
        return this.participantList;
    }

    public AssetList getLetterOfCreditList() {
        return this.letterOfCreditList;
    }

    public LetterOfCreditClientIdentity getClientIdentity() {
        return this.clientIdentity;
    }
}