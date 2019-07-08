package org.locnet.assets;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class Approval {

    public static Approval deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);
        
        boolean applicant = jsonObject.getBoolean("applicant");
        boolean beneficiary = jsonObject.getBoolean("beneficiary");
        boolean issuingBank = jsonObject.getBoolean("issuingBank");
        boolean exportingBank = jsonObject.getBoolean("exportingBank");
        
        return new Approval(applicant, beneficiary, issuingBank, exportingBank);
    }

    @Property()
    private boolean applicant;

    @Property()
    private boolean beneficiary;

    @Property()
    private boolean issuingBank;

    @Property()
    private boolean exportingBank;

    public Approval(boolean applicant, boolean beneficiary, boolean issuingBank, boolean exportingBank) {
        this.applicant = applicant;
        this.beneficiary = beneficiary;
        this.issuingBank = issuingBank;
        this.exportingBank = exportingBank;
    }

    public void setApplicant() {
        this.applicant = true;
    }

    public boolean getApplicant() {
        return this.applicant;
    }

    public void setBeneficiary() {
        this.beneficiary = true;
    }

    public boolean getBeneficiary() {
        return this.beneficiary;
    }

    public void setIssuingBank() {
        this.issuingBank = true;
    }

    public boolean getIssuingBank() {
        return this.issuingBank;
    }

    public void setExportingBank() {
        this.exportingBank = true;
    }

    public boolean getExportingBank() {
        return this.exportingBank;
    }

    public void clearApproval() {
        this.applicant = false;
        this.beneficiary = false;
        this.issuingBank = false;
        this.exportingBank = false;
    }
    
    public boolean isFullyApproved() {
        return this.applicant && this.beneficiary && this.issuingBank && this.exportingBank;
    }
}