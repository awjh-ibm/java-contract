package org.locnet;

import org.awjh.ledger_api.Util;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.locnet.utils.LetterOfCreditContext;

public class BaseContract implements ContractInterface {
    public BaseContract() {
    }

    @Override
    public LetterOfCreditContext createContext(ChaincodeStub stub) {
        try {
            return new LetterOfCreditContext(stub);
        } catch (Exception err) {
            Util.logStackTrace(err);
            return null;
        }
    }
}
