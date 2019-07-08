package org.locnet.utils;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.protobuf.InvalidProtocolBufferException;

import org.awjh.Asset;
import org.awjh.Organization;
import org.awjh.Participant;
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

    public LetterOfCreditContext(ChaincodeStub stub)
            throws InvalidProtocolBufferException, CertificateException, UnsupportedEncodingException {
        super(stub);

        final Map<String, Function<String, Organization>> organizationDeserializers = new HashMap<String, Function<String, Organization>>();
        organizationDeserializers.put("Bank", Bank::deserialize);

        final Map<String, Function<String, Participant>> participantDeserializers = new HashMap<String, Function<String, Participant>>();
        participantDeserializers.put("Task", Task::deserialize);

        final Map<String, Function<String, Asset>> assetDeserializers = new HashMap<String, Function<String, Asset>>();
        assetDeserializers.put("LetterOfCredit", LetterOfCredit::deserialize);

        this.organizationList = new OrganizationList(this, "locnet.organizations", organizationDeserializers);
        this.participantList = new ParticipantList(this, "locnet.participants", participantDeserializers);
        this.letterOfCreditList = new AssetList(this, "locnet.assets", assetDeserializers);

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