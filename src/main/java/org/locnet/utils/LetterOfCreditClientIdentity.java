package org.locnet.utils;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import org.awjh.ClientIdentity;
import org.awjh.Organization;
import org.awjh.Participant;
import org.locnet.Constants;
import org.locnet.organizations.Bank;
import org.locnet.participants.Task;

public class LetterOfCreditClientIdentity extends ClientIdentity {
    private static String ID_FIELD = "locnet.username";
    private static String ORG_NAME_FIELD = "locnet.company";

    private Participant participant;
    private Organization organization;

    public LetterOfCreditClientIdentity(LetterOfCreditContext ctx) throws InvalidProtocolBufferException, CertificateException, UnsupportedEncodingException {
        super(ctx.getStub());

        final String participantId = this.getAttributeValue(LetterOfCreditClientIdentity.ID_FIELD);

        if (ctx.getParticipantList().exists(participantId)) {
            this.participant = ctx.getParticipantList().get(participantId);
            this.organization = ctx.getOrganizationList().get(this.participant.getOrganizationId());
        } else {
            final String orgId = participantId.split("@")[1];
            ArrayList<String> roles = new ArrayList<String>();

            for (Map.Entry<String, String> entry: this.attrs.entrySet()) {
                final String attr = entry.getKey();
                final String value = entry.getValue();

                if (attr.startsWith(Constants.ROLES_PREFIX) && value.equals("y")) {
                    roles.add(attr.split(Constants.ROLES_PREFIX)[1]);
                }
            }

            final String[] rolesArr = roles.toArray(new String[roles.size()]);

            this.participant = new Task(participantId, rolesArr, orgId);
            ctx.getParticipantList().add(this.participant);

            if (ctx.getOrganizationList().exists(orgId)) {
                this.organization = ctx.getOrganizationList().get(orgId);
            } else {
                this.organization = new Bank(orgId, this.getAttributeValue(LetterOfCreditClientIdentity.ORG_NAME_FIELD));
                ctx.getOrganizationList().add(this.organization);
            }
        }
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public Organization getOrganization() {
        return this.organization;
    }
}