package ch.admin.bag.covidcertificate.api.request;

public enum MessageType {
    INFO("INFO"),
    WARNING("WARNING");

    public String getValue() {
        return value;
    }

    private String value;

    MessageType(String value) {
        this.value = value;
    }
}
