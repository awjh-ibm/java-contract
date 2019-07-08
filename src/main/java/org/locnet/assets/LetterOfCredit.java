package org.locnet.assets;

import org.awjh.Asset;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locnet.enums.Status;

@DataType()
public class LetterOfCredit extends Asset {

    public static LetterOfCredit deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String id = jsonObject.getString("id");
        String applicantId = jsonObject.getString("applicantId");
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

        Status status = Status.values()[jsonObject.getInt("Status")];

        return new LetterOfCredit(id, applicantId, beneficiaryId, issuingBankId, exportingBankId, rules, productDetails, evidence, approval, status);
    }

    @Property()
    private String applicantId;

    @Property()
    private String beneficiaryId;

    @Property()
    private String issuingBankId;

    @Property()
    private String exportingBankId;

    @Property()
    private String[] rules;

    @Property()
    private ProductDetails productDetails;

    @Property()
    private Evidence[] evidence;

    @Property()
    private Approval approval;

    @Property()
    private Status status;

    public LetterOfCredit(String id, String applicantId, String beneficiaryId, String issuingBankId, String exportingBankId, String[] rules, ProductDetails productDetails, Evidence[] evidence, Approval approval, Status status) {
        super(id, "LetterOfCredit");

        this.applicantId = applicantId;
        this.beneficiaryId = beneficiaryId;
        this.issuingBankId = issuingBankId;
        this.exportingBankId = exportingBankId;
        this.rules = rules;
        this.productDetails = productDetails;
        this.evidence = evidence;
        this.approval = approval;
        this.status = status;
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
