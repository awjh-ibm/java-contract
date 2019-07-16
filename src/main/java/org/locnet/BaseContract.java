package org.locnet;

import java.io.PrintWriter;
import java.io.StringWriter;
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
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            err.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Logger.getLogger("HELLO").warning("FAILED TO MAKE CONTEXT -> " + sStackTrace);
            return null;
        }
    }
}