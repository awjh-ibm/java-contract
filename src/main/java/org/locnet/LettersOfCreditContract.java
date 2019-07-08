/*
 * SPDX-License-Identifier: 
 */
package org.locnet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.json.JSONObject;
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
        return ctx.getClientIdentity().getAttributeValue("someAttr");
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