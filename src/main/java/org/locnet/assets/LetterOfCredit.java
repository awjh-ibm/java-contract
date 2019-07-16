package org.locnet.assets;

import java.lang.reflect.Field;

import org.awjh.Asset;
import org.awjh.ledger_api.Private;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locnet.Constants;
import org.locnet.enums.Status;

@DataType()
public class LetterOfCredit extends Asset {

    public static LetterOfCredit deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        // PUBLIC FIELDS
        String id = jsonObject.getString("id");
        String applicantId = jsonObject.getString("applicantId");
        Status status = Status.values()[jsonObject.getInt("Status")];
        Double value = jsonObject.getDouble("value");

        // CHECK IF HAS ALL PRIVATE FIELDS. THIS CLASS ONLY HAS ONE TYPE OF PRIVATE DATA MAY NEED DIFFERENT METHOD OF WORKING
        // OUT WHICH CONSTRUCTOR TO CALL ON MORE COMPLEX OBJECTS
        boolean hasAllPrivate = true;
        for (Field field : LetterOfCredit.class.getDeclaredFields()) {
            final Private annotation = field.getAnnotation(Private.class);
            if (annotation != null && !jsonObject.has(field.getName())) {
                hasAllPrivate = false;
                break;
            }
        }

        if (hasAllPrivate) {
            // PRIVATE FIELDS
            String beneficiaryId = jsonObject.getString("beneficiaryId");
            String issuingBankId = jsonObject.getString("issuingBankId");
            String exportingBankId = jsonObject.getString("exportingBankId");

            JSONArray rulesJSON = jsonObject.getJSONArray("rules");
            String[] rules = new String[rulesJSON.length()];
            for (int i = 0; i < rulesJSON.length(); i++) {
                rules[i] = rulesJSON.getString(i);
            }

            ProductDetails productDetails = ProductDetails.deserialize(jsonObject.getJSONObject("productDetails").toString());

            JSONArray evidenceJSON = jsonObject.getJSONArray("evidence");
            Evidence[] evidence = new Evidence[evidenceJSON.length()];
            for (int i = 0; i < evidenceJSON.length(); i++) {
                evidence[i] = Evidence.deserialize(evidenceJSON.getJSONObject(i).toString());
            }

            Approval approval = Approval.deserialize(jsonObject.getJSONObject("productDetails").toString());

            return new LetterOfCredit(id, applicantId, beneficiaryId, issuingBankId, exportingBankId, rules, productDetails, evidence, approval, status);
        } else {
            return new LetterOfCredit(id, applicantId, status, value);
        }
    }

    @Property()
    private String applicantId;

    @Property()
    @Private(collections = {
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private String beneficiaryId;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private String issuingBankId;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private String exportingBankId;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private String[] rules;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private ProductDetails productDetails;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private Evidence[] evidence;

    @Property()
    @Private(collections = { 
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_BANK_OF_DINERO,
        Constants.PRIVATE_COLLECTIONS.EASTWOOD_BANKING_AND_NICHOLSON,
        Constants.PRIVATE_COLLECTIONS.BANK_OF_DINERO_AND_NICHOLSON
    })
    private Approval approval;

    @Property()
    private Status status;

    @Property()
    private Double value;

    public LetterOfCredit(String id, String applicantId, String beneficiaryId, String issuingBankId, String exportingBankId, String[] rules, ProductDetails productDetails, Evidence[] evidence, Approval approval, Status status) {
        super(id, LetterOfCredit.class.getName());

        this.applicantId = applicantId;
        this.beneficiaryId = beneficiaryId;
        this.issuingBankId = issuingBankId;
        this.exportingBankId = exportingBankId;
        this.rules = rules;
        this.productDetails = productDetails;
        this.evidence = evidence;
        this.approval = approval;
        this.status = status;
        this.value = productDetails.getQuantity() * productDetails.getUnitPrice();
    }

    private LetterOfCredit(String id, String applicantId, Status status, Double value) {
        // When we aren't able to see the private data
        super(id, LetterOfCredit.class.getName());

        this.applicantId = applicantId;
        this.status = status;
        this.value = value;
    }

    public String getApplicantId() {
        return this.applicantId;
    }

    public String getBeneficiaryId() {
        return this.beneficiaryId;
    }

    public String getIssuingBankId() {
        return this.issuingBankId;
    }

    public String getExportingBankId() {
        return this.exportingBankId;
    }

    public String[] getRules() {
        return this.rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public ProductDetails getProductDetails() {
        return this.productDetails;
    }

    public Evidence[] getEvidence() {
        return this.evidence;
    }

    public void addEvidence(Evidence evidence) {
        int evidenceLength = this.evidence.length;
        Evidence[] newEvidence = new Evidence[evidenceLength + 1];

        for (int i = 0; i < evidenceLength; i++) {
            newEvidence[i] = this.evidence[i];
        }

        newEvidence[evidenceLength + 1] = evidence;

        this.evidence = newEvidence;
    }

    public Approval getApproval() {
        return this.approval;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
