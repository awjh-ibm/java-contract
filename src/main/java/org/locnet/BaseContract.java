package org.locnet;

import java.util.logging.Logger;

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
            Logger.getLogger("HELLO").warning("FAILED TO MAKE CONTEXT -> " + err.getStackTrace().toString());
            return null;
        }
    }
}