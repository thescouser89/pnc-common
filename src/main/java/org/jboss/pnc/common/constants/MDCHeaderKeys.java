package org.jboss.pnc.common.constants;

public enum MDCHeaderKeys {

    USER_ID("log-user-id", MDCKeys.USER_ID_KEY),
    REQUEST_CONTEXT("log-request-context", MDCKeys.REQUEST_CONTEXT_KEY),
    PROCESS_CONTEXT("log-process-context", MDCKeys.PROCESS_CONTEXT_KEY),
    TMP("log-tmp", MDCKeys.TMP_KEY),
    EXP("log-exp", MDCKeys.EXP_KEY);

    private final String headerName;
    private final String mdcKey;

    MDCHeaderKeys(String headerName, String mdcKey) {
        this.headerName = headerName;
        this.mdcKey = mdcKey;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getMdcKey() {
        return mdcKey;
    }
}
