package eu.seal.is.model;

import java.util.Map;


public class MngrSessionTO {

    private String sessionId;
    private Map sessionVariables;

    public MngrSessionTO(String sessionId, Map sessionVariables) {
        this.sessionId = sessionId;
        this.sessionVariables = sessionVariables;
    }

    public MngrSessionTO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(Map sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

}
